package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.User;
import com.databuck.config.AppConfig;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.dao.IGroupDAO;

@Repository
public class GroupDAOImpl implements IGroupDAO {

	
	@Autowired
	private Properties activeDirectoryConnectionProperties;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public static Hashtable<String, Object> object = new Hashtable<String, Object>();
	
	private static final Logger LOG = Logger.getLogger(GroupDAOImpl.class);

	@Override
	public List<User> getAllGroups() {
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
				
		String sql = "SELECT idUser, firstName, lastName,salt,company,department,email from "+user_table;
		List<User> userList = jdbcTemplate.query(sql, new RowMapper<User>() {
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();

				user.setIdUser(rs.getLong("idUser"));
				user.setFirstName(rs.getString("firstName"));
				user.setLastName(rs.getString("lastName"));
				user.setSalt(rs.getString("salt"));
				user.setCompany(rs.getString("company"));
				user.setDepartment(rs.getString("department"));
				user.setEmail(rs.getString("email"));
				
				return user;
			}

		});
		return userList;
	}

	@Override
	public List<User> getAllassignGroups(Long projectId) {
		List<User> Usersassigned = getAllassignuserfromnormalassociationtable(projectId);
		
		LOG.debug("old asssigned Databuck Users-->" + Usersassigned);
		return Usersassigned;
	}

	@Override
	public List<User> getAllGroupsfromActiveDirectory() {
		/*Properties Propfile = appconfig.activeDirectoryConnectionProperties();*/
		
		Hashtable<String, Object> object = new Hashtable<String, Object>();
		List<User> Users = new ArrayList<User>() ;
		List<User> Users1 = new ArrayList<User>() ;
		
		HashSet<User> alldupusers = new HashSet<User>();
		String usersContainer = activeDirectoryConnectionProperties.getProperty("searchBase");//user search base domain container
		String userObjectClass = activeDirectoryConnectionProperties.getProperty("userObjectClass");//user userObjectClass

		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, activeDirectoryConnectionProperties.getProperty("context"));
		properties.put(Context.PROVIDER_URL,  activeDirectoryConnectionProperties.getProperty("url"));
		properties.put(Context.SECURITY_AUTHENTICATION, activeDirectoryConnectionProperties.getProperty("auth"));
		properties.put(Context.SECURITY_PRINCIPAL, activeDirectoryConnectionProperties.getProperty("principal")); 
		properties.put(Context.SECURITY_CREDENTIALS, activeDirectoryConnectionProperties.getProperty("credentials"));
		try {
			DirContext context = new InitialDirContext(properties);
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass="+userObjectClass+")";       
			String pipeDelimited = activeDirectoryConnectionProperties.getProperty("Domainforalluser");
			LOG.debug("domainfilter -> "+pipeDelimited);
			
		//	String pipeDelimited = "ou=Firsteigen,ou=Databuck,dc=example,dc=com|ou=I2I,ou=Databuck,dc=example,dc=com"; 
			
			String[] Arraydomain = pipeDelimited.split("\\|");//commented for future
			//String[] Arraydomain = usersContainer.split("\\|");
			LOG.debug("splited domains -> "+Arrays.toString(Arraydomain));
			LOG.debug("Arraydomain.length -> "+Arraydomain.length);
			
			for(int i=0 ; i<Arraydomain.length; i++){
			NamingEnumeration values = context.search(Arraydomain[i],filter,searchCtrls);
			while (values.hasMoreElements())
			{
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs)
				{
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();)
					{
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
							Object temp = vals.nextElement();
							object.put(attributeID, temp);
							
					};				
				}		
					//String pass = object.get("userPassword").toString();
					//LOG.debug("pass->"+pass);
					//String uid = object.get("uid").toString();
					//LOG.debug("uid->"+uid);
					String attributetofetch = activeDirectoryConnectionProperties.getProperty("attributetofetch");
					String  idUser =  object.get(attributetofetch).toString();
					//String  idUser =  object.get("employeeNumber").toString();
					LOG.debug("idUser->"+idUser);
					String cn = object.get("cn").toString();
					LOG.debug("cn->"+cn);
					//Amit logic
					/*idUser=idUser.toUpperCase();
			           
			           long result1 = 0;
			                for (int i = 0;i<idUser.length();i++)
			                {
			                	result1= (idUser.charAt(i) - 'A' + 1) + result1*26;
			 
			                }*/
		
					Users.add(new User(0L,idUser,null,null,idUser));		
				
			}			
			}
		}
			context.close();
		} catch (NamingException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		
		boolean flag = true;
		 ArrayList<User> newList = new ArrayList<User>(); 
		  
	       
	        for (User element : Users) { 
	            for (User newList1 : newList) {            
	             if(element.getEmail().equalsIgnoreCase(newList1.getEmail()))
	             {
	            	  flag = false;
	            	  break;
	             }
	                 }
	            if(flag) {
		              newList.add(element); 
		              }
	            flag = true;
	         } 
	       
	
	
			LOG.debug("unique newList-->" + newList);
			return newList;
	    }
		
		
		
		

	

	@Override
	public List<User> getAllassignGroupsfromActiveDirectory(Long projectId) {
		/*Properties Propfile = appconfig.activeDirectoryConnectionProperties();*/
		List<User> Usersassigned = getAllassignuserfromassociationtable(projectId);
		/*Hashtable<String, Object> object = new Hashtable<String, Object>();
		List<User> Users = new ArrayList<User>();
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, activeDirectoryConnectionProperties.getProperty("context"));
		properties.put(Context.PROVIDER_URL,  activeDirectoryConnectionProperties.getProperty("url"));
		properties.put(Context.SECURITY_AUTHENTICATION, activeDirectoryConnectionProperties.getProperty("auth"));
		properties.put(Context.SECURITY_PRINCIPAL, activeDirectoryConnectionProperties.getProperty("principal")); 
		properties.put(Context.SECURITY_CREDENTIALS, activeDirectoryConnectionProperties.getProperty("credentials"));
		try {
			DirContext context = new InitialDirContext(properties);
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=person)";       
			NamingEnumeration values = context.search(activeDirectoryConnectionProperties.getProperty("Domainforalluser"),filter,searchCtrls);
			
		
			while (values.hasMoreElements())
			{
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs)
				{
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();)
					{
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
							Object temp = vals.nextElement();
							object.put(attributeID, temp);
					};				
				}		
					//String pass = object.get("userPassword").toString();
					//LOG.debug("pass->"+pass);
					//String uid = object.get("uid").toString();
					//LOG.debug("uid->"+uid);
					String attributetofetch = activeDirectoryConnectionProperties.getProperty("attributetofetch");
					String  idUser =  object.get(attributetofetch).toString();
					//String  idUser =  object.get("employeeNumber").toString();
					LOG.debug("idUser->"+idUser);
					String cn = object.get("cn").toString();
					LOG.debug("cn->"+cn);
					
					
					//Long defaultuserid = 1000l ;
					//obj.setIdUser(Long.parseLong(idUser));
					
					//String sn = object.get("sn").toString();
					//LOG.debug("sn->"+sn);
					Users.add(new User(0L,cn,null,null,idUser));				
			}			
			}
			context.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}*/
		LOG.debug("old asssigned Users-->" + Usersassigned);
		return Usersassigned;
}

	@Override
	public List<User> getAllassignuserfromassociationtable(Long projectId) {
		String sql = "SELECT * from projecttoActDiruser "
				+ " where idProject =" + projectId + ";";

		List<User> assignedUsers = jdbcTemplate.query(sql, new RowMapper<User>() {
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
                 user.setEmail(rs.getString("idUser"));
				//user.setIdUser(rs.getLong("idUser"));
				user.setFirstName(rs.getString("firstName"));
				user.setLastName(rs.getString("lastName"));

				return user;
			}

		});
		return assignedUsers;
		
		
	
	}

	@Override
	public List<User> getAllassignuserfromnormalassociationtable(Long projectId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * from projecttouser "
				+ " where idProject =" + projectId + ";";

		List<User> assignedUsers = jdbcTemplate.query(sql, new RowMapper<User>() {
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
                 user.setEmail(rs.getString("idUser"));
				//user.setIdUser(rs.getLong("idUser"));
				//user.setFirstName(rs.getString("firstName"));
				//user.setLastName(rs.getString("lastName"));

				return user;
			}

		});
		return assignedUsers;
	}
	

	/*public int deleteGroup(Long idGroup) {

		try {
			int update = jdbcTemplate.update("DELETE FROM projgroup WHERE idGroup = ?", new Object[] { idGroup });
			return 1;
		} catch (Exception e) {

			return 2;
		}

	}*/

	/*public List<Group> getPaginationGroups(int start, int numRecords, String sSearch) {
		List<Group> grouplist = new ArrayList<>();

		String searchSQL = "";

		try {

			String sql = "select * from projgroup";

			String globeSearch = " Where idGroup like '%" + sSearch + "%' or groupName like '%" + sSearch
					+ "%' or description like '%" + sSearch + "%'";

			if (sSearch != "") {
				sql = sql + globeSearch;
			}

			sql += " limit " + start + ", " + numRecords;

			LOG.debug("Sql :" + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			// int count = 0;
			// while(queryForRowSet.next()){
			// count++;
			// }
			// LOG.debug("Size" +count);
			while (queryForRowSet.next()) {

				Group group = new Group();
				group.setIdGroup(queryForRowSet.getLong("idGroup"));
				group.setGroupName(queryForRowSet.getString("groupName"));
				group.setGroupDescription(queryForRowSet.getString("description"));

				grouplist.add(group);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grouplist;
	}

	public int getTotalRecordCount() {
		int totalRecords = -1;
		String sql = "select count(*) as count from projgroup";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalRecords = results.getInt("count");
		}
		return totalRecords;
	}

	public int getTotalDisplayRecords(String sSearch) {
		int totalDisplayRecords = -1;
		String sql = "select count(*) as count from projgroup Where idGroup like '%" + sSearch
				+ "%' or groupName like '%" + sSearch + "%' or description like '%" + sSearch + "%'";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalDisplayRecords = results.getInt("count");
		}
		return totalDisplayRecords;
	}

	public int insertUserToGroupAssociation(Long groupId, Long userId) {
		String sql = "insert into grouptouser(idGroup, idUser) " + " VALUES (?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, groupId);
				pst.setLong(2, userId);

				return pst;
			}
		});

		return update;
	}

	public List<Group> getAssociatedGroups(Long idProject, String flag) {

		String sql = "SELECT A.idGroup as grpId, groupName from projecttogroup A join projgroup P"
				+ " on A.idGroup = P.idGroup" + " where A.idProject =" + idProject + " AND A.isOwner= '" + flag + "' ";

		List<Group> groupList = jdbcTemplate.query(sql, new RowMapper<Group>() {
			public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
				Group group = new Group();

				group.setIdGroup(rs.getLong("grpId"));
				group.setGroupName(rs.getString("groupName"));

				return group;
			}

		});
		return groupList;
	}

	public int addAssociatedOwn(Long groupId, Long userId) {
		String sql = "insert into grouptouser(idGroup, idUser) " + " VALUES (?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, groupId);
				pst.setLong(2, userId);

				return pst;
			}
		});

		return update;

	}

	public int delAssociatedOwn(String delValues) {

		int update = jdbcTemplate.update("DELETE FROM grouptouser WHERE idUser IN " + "(" + delValues + ")");

		return update;

	}

	public List<User> getAssociatedUser(Long idGroup) {

		String sql = "SELECT P.idUser as idUser, P.firstName from grouptouser A join User P" + " on A.idUser = P.idUser"
				+ " where A.idGroup =" + idGroup + ";";

		List<User> userList = jdbcTemplate.query(sql, new RowMapper<User>() {
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();

				user.setIdUser(rs.getLong("idUser"));
				user.setFirstName(rs.getString("firstName"));

				return user;
			}

		});
		return userList;
	}
*/
}
