package com.databuck.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.Domain;
import com.databuck.bean.Project;
import com.databuck.bean.User;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ILoginDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.econstants.DatabuckPropertyCategory;
import com.databuck.util.JwfSpaInfra;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private ILoginDAO ILoginDAO;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Properties activeDirectoryConnectionProperties;
	@Autowired
	public IUserService userservice;
	@Autowired
	IUserDAO userDAO;
	@Autowired
	public Properties licenseProperties;
	@Autowired
	public ITaskDAO taskDao;
	
	private static final Logger LOG = Logger.getLogger(LoginServiceImpl.class);

	public static Hashtable<String, String> env = new Hashtable<String, String>();
	public static Hashtable<String, Object> object = new Hashtable<String, Object>();
	public static Hashtable<String, Object> objectrole = new Hashtable<String, Object>();
	public static String Role;
	public static ArrayList<String> groupslist = new ArrayList<String>();

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	private ILoginDAO loginDAO;

	public boolean validateIdRole(long idRole) {
		int dbIdRole = 0;
		// Query compatibility changes for both POSTGRES and MYSQL
		String role = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"role\"" : "Role";
		String query = "select idrole from " + role + " where idrole=" + idRole;

		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);

		while (queryForRowSet.next()) {
			dbIdRole = queryForRowSet.getInt(1);
		}
		try {
			if (dbIdRole > 0) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getFirstNameFromUserTable(Long idUser) {

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";
		String query = "select firstName,COALESCE(lastName,'') from " + user_table + " where idUser=" + idUser;

		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);
		String firstName = "";
		while (queryForRowSet.next()) {
			firstName = queryForRowSet.getString(1) + " " + queryForRowSet.getString(2);
		}
		return firstName.trim();
	}

	@Override
	public Long userAuthentication(String email, String password) {
		return loginDAO.userAuthentication(email, password);
	}

	public Long getRolesFromUserRoleTable(Long idUser) {
		String query = "select idRole from UserRole where idUser=" + idUser;
		Long idRole = jdbcTemplate.queryForObject(query, Long.class);
		return idRole;
	}

	public SqlRowSet getIdTaskandAccessControlFromRoleModuleTable(Long idRole) {
		String query = "select * from RoleModule where idRole=" + idRole;
		SqlRowSet roleModuleTable = jdbcTemplate.queryForRowSet(query);
		return roleModuleTable;
	}

	public String getTaskNameFromModuleTable(long idTask) {
		String query = "select taskName from Module where idTask=" + idTask;
		String taskName = jdbcTemplate.queryForObject(query, String.class);
		return taskName;
	}

	@Override
	public User userActiveDirectoryAuthentication(String email, String password) throws Exception {

		String User = email;
		String Password = password;
		String domainname = getUid(User);
		LOG.debug("Domain name -->" + domainname);
		User obj = new User();

		if (domainname != null) {
			/* Found user - test password */

			if (testBind(domainname, Password)) {
				LOG.debug("user '" + User + "'authentication succeeded");
				Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
				ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY,
						activeDirectoryConnectionProperties.getProperty("context"));
				ldapEnv.put(Context.PROVIDER_URL, activeDirectoryConnectionProperties.getProperty("url"));
				ldapEnv.put(Context.SECURITY_AUTHENTICATION, activeDirectoryConnectionProperties.getProperty("auth"));
				ldapEnv.put(Context.SECURITY_PRINCIPAL, activeDirectoryConnectionProperties.getProperty("principal"));
				ldapEnv.put(Context.SECURITY_CREDENTIALS,
						activeDirectoryConnectionProperties.getProperty("credentials"));
				String dn = null;

				try {

					DirContext ldapContext = new InitialDirContext(ldapEnv);
					LOG.debug(ldapContext);
					String Loginuid = activeDirectoryConnectionProperties.getProperty("Loginuid");
					String filter = "(" + Loginuid + "=" + User + ")";
					SearchControls searchCtrls = new SearchControls();
					searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
					NamingEnumeration values = ldapContext.search(domainname, filter, searchCtrls);
					while (values.hasMoreElements()) {
						SearchResult result = (SearchResult) values.next();
						Attributes attribs = result.getAttributes();
						if (null != attribs) {
							for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
								Attribute atr = (Attribute) ae.next();
								String attributeID = atr.getID();
								for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
									Object temp = vals.nextElement();
									LOG.debug(attributeID + ": " + temp);
									object.put(attributeID, temp);
								}
								;

							}
						}
					}

					ldapContext.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}

				LOG.debug("object-->" + object);
				// String pass = object.get("userPassword").toString();
				// String uid = object.get("uid").toString();
				String attributetofetch = activeDirectoryConnectionProperties.getProperty("attributetofetch");
				String idUser = object.get(attributetofetch).toString();
				// String idUser = object.get("sAMAccountName").toString();
				String cn = object.get("cn").toString();

				obj.setFirstName(cn);

				obj.setEmail(idUser);
				// obj.setIdUser(result);
				LOG.debug("User object-->" + obj);
				// return "authentication succeeded";
				return obj;
				// System.exit(0);
			} else {
				LOG.debug("user '" + User + "'authentication failed");
				return null;
			}
		} else {
			LOG.debug("LDAP server conection failed to connect and Domainname ->" + domainname);
			return null;
		}

	}

	private String getUid(String user) throws Exception {

		Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, activeDirectoryConnectionProperties.getProperty("context"));
		ldapEnv.put(Context.PROVIDER_URL, activeDirectoryConnectionProperties.getProperty("url"));
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, activeDirectoryConnectionProperties.getProperty("auth"));
		ldapEnv.put(Context.SECURITY_PRINCIPAL, activeDirectoryConnectionProperties.getProperty("principal"));
		ldapEnv.put(Context.SECURITY_CREDENTIALS, activeDirectoryConnectionProperties.getProperty("credentials"));

		String dn = null;

		try {

			DirContext ldapContext = new InitialDirContext(ldapEnv);
			LOG.debug(ldapContext);
			String Loginuid = activeDirectoryConnectionProperties.getProperty("Loginuid");
			String filter = "(" + Loginuid + "=" + user + ")";
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchBase = activeDirectoryConnectionProperties.getProperty("searchBase");
			NamingEnumeration answer = ldapContext.search(searchBase, filter, ctrl);
			if (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();
				dn = result.getNameInNamespace();
			} else {
				dn = null;
			}
			answer.close();
			return dn;

		} catch (NamingException e) {

			e.printStackTrace();
		}
		return dn;

	}

	private DirContext ldapContext() throws Exception {
		return ldapContext(env);
	}

	private DirContext ldapContext(Hashtable<String, String> env) throws Exception {
		LOG.debug("context->" + activeDirectoryConnectionProperties.getProperty("context"));
		LOG.debug("url->" + activeDirectoryConnectionProperties.getProperty("url"));
		LOG.debug("auth->" + activeDirectoryConnectionProperties.getProperty("auth"));
		LOG.debug("principal->" + activeDirectoryConnectionProperties.getProperty("principal"));
		// LOG.debug("credentials->" +
		// activeDirectoryConnectionProperties.getProperty("credentials"));
		env.put(Context.INITIAL_CONTEXT_FACTORY, activeDirectoryConnectionProperties.getProperty("context"));
		env.put(Context.PROVIDER_URL, activeDirectoryConnectionProperties.getProperty("url"));

		DirContext ctx = new InitialDirContext(env);
		return ctx;
	}

	private boolean testBind(String dn, String password) throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.SECURITY_AUTHENTICATION, activeDirectoryConnectionProperties.getProperty("auth"));
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			LOG.debug("env-->" + env);
			ldapContext(env);
		} catch (javax.naming.AuthenticationException e) {
			return false;
		}
		return true;
	}

	@Override
	public String getRoleNamefromActiveDirectory(String email, String password) throws Exception {
		String Useruid = email;// uid of login ldap user
		String UserPassword = password;// password of ldap login user
		String ldapUri = activeDirectoryConnectionProperties.getProperty("url");// provider URL
		String usersContainer = activeDirectoryConnectionProperties.getProperty("searchBase");// user search base domain
																								// container
		String Domainforalluser = activeDirectoryConnectionProperties.getProperty("Domainforalluser");// Domainforalluser
		String username = activeDirectoryConnectionProperties.getProperty("principal");// principal
		String decryptedpassword = activeDirectoryConnectionProperties.getProperty("credentials");// credential
		String Loginuid = activeDirectoryConnectionProperties.getProperty("Loginuid");// Loginuid
		String rolePosition = activeDirectoryConnectionProperties.getProperty("rolePosition");// rolePosition
		String groupFilter = activeDirectoryConnectionProperties.getProperty("groupFilter");// rolePosition
		/*
		 * StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		 * decryptor.setPassword("4qsE9gaz%!L@UMrK5myY"); String decryptedText =
		 * decryptor.decrypt(encryptedpassword);
		 */// decrypted password
		Hashtable context = new Hashtable();
		context.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		context.put(Context.PROVIDER_URL, ldapUri);
		context.put(Context.SECURITY_AUTHENTICATION, "simple");
		context.put(Context.SECURITY_PRINCIPAL, username);
		context.put(Context.SECURITY_CREDENTIALS, decryptedpassword);

		try {
			DirContext ctx = new InitialDirContext(context);
			SearchControls ctls = new SearchControls();
			String[] attrIDs = { "cn" };
			ctls.setReturningAttributes(attrIDs);
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); // '(&(objectClass=groupOfNames)(cn=1AOE*)(member=uid=dbreader,ou=people,dc=wfb,dc=corp))'
			NamingEnumeration answer = ctx.search(usersContainer, "(&(objectClass=groupOfNames)(cn=" + groupFilter
					+ "*)(member=" + Loginuid + "=" + Useruid + "," + Domainforalluser + "))", ctls);
			LOG.debug(" groups under domain : " + usersContainer);
			LOG.debug(" group of " + Useruid);
			while (answer.hasMore()) {
				SearchResult rslt = (SearchResult) answer.next();
				Attributes attrs = rslt.getAttributes();
				// LOG.debug("cn : "+attrs.get("cn"));
				String groups = attrs.get("cn").toString();
				String[] groupname = groups.split(":");
				String userGroup = groupname[1];
				LOG.debug("Absolute Groupname  : " + userGroup);
				/*
				 * if(userGroup.contains("1AOE")) { String [] groupnamesplit =
				 * userGroup.split("[.]"); String gruopspit = groupnamesplit[4];
				 * LOG.debug("Groupname  = "+gruopspit); Role = gruopspit; }
				 */
				String[] groupnamesplit = userGroup.split("[.]");
				int positionint = Integer.parseInt(rolePosition);
				String grpsplit = groupnamesplit[positionint];
				LOG.debug("Groupname  = " + grpsplit);
				Role = grpsplit;
			}

			ctx.close();

		}

		catch (NamingException e) {
			System.err.println("Problem searching directory: " + e);
		}

		return Role;
	}

	@Override
	public ArrayList<String> getAllRoleNamefromActiveDirectory() throws Exception {
		String ldapUri = activeDirectoryConnectionProperties.getProperty("url");// provider URL
		String usersContainer = activeDirectoryConnectionProperties.getProperty("searchBase");// user search base domain
																								// container
		String username = activeDirectoryConnectionProperties.getProperty("principal");// principal
		String password = activeDirectoryConnectionProperties.getProperty("credentials");// credential
		String rolePosition = activeDirectoryConnectionProperties.getProperty("rolePosition");// rolePosition
		String groupFilter = activeDirectoryConnectionProperties.getProperty("groupFilter");// rolePosition
		/*
		 * StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		 * decryptor.setPassword("4qsE9gaz%!L@UMrK5myY"); String decryptedText =
		 * decryptor.decrypt(password);
		 */
		Hashtable context = new Hashtable();
		context.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		context.put(Context.PROVIDER_URL, ldapUri);
		context.put(Context.SECURITY_AUTHENTICATION, "simple");
		context.put(Context.SECURITY_PRINCIPAL, username);
		context.put(Context.SECURITY_CREDENTIALS, password);

		try {
			DirContext ctx = new InitialDirContext(context);
			SearchControls ctls = new SearchControls();
			String[] attrIDs = { "cn" };
			ctls.setReturningAttributes(attrIDs);
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); // "(&(objectclass=groupOfNames)(cn=1AOE*))"
			NamingEnumeration answer = ctx.search(usersContainer,
					"(&(objectclass=groupOfNames)(cn=" + groupFilter + "*))", ctls);
			LOG.debug("LIST of groups under domain : " + usersContainer);
			while (answer.hasMore()) {
				SearchResult rslt = (SearchResult) answer.next();
				Attributes attrs = rslt.getAttributes();
				// LOG.debug("cn : "+attrs.get("cn"));
				String groups = attrs.get("cn").toString();
				String[] groupname = groups.split(":");
				String userGroup = groupname[1];
				LOG.debug(userGroup);
				/*
				 * if(userGroup.contains("1AOE")) { String [] groupnamesplit =
				 * userGroup.split("[.]"); String grpsplit = groupnamesplit[4];
				 * 
				 * LOG.debug("gruopspit = "+grpsplit); groupslist.add(grpsplit); }
				 */
				String[] groupnamesplit = userGroup.split("[.]");
				int positionint = Integer.parseInt(rolePosition);
				String grpsplit = groupnamesplit[positionint];
				LOG.debug("Groupname  = " + grpsplit);
				groupslist.add(grpsplit);

			}
			ctx.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		LOG.debug("groupslist = " + groupslist);
		return groupslist;
	}

	@Override
	public Long getRoleIdFromRoleTable(String roleName) {

		return ILoginDAO.getRoleIdFromRoleTable(roleName);
	}

	@Override
	public Long getRoleMapToLdapGroupFromGroupRolemapTable(String ldapGroup) {
		String query = "select distinct idRole from databuck_security_matrix where ldap_group_name = " + "'" + ldapGroup
				+ "'";
		Long idRole = jdbcTemplate.queryForObject(query, Long.class);
		return idRole;
	}

	@Override
	public String getRoleFromRoleTable(Long idRole) {
		String query = "select roleName from Role where idRole =" + idRole;
		String roleName = jdbcTemplate.queryForObject(query, String.class);
		return roleName;
	}

	@Override
	public List<Project> getAllProjectsOfARole(String ldapGroup, Long dRole) {

		List<Integer> dlist = new ArrayList<Integer>();

		String sqld = "";
		/*
		 * sqld = "Select  p.idProject from databuck_security_matrix p " +
		 * "where p.idRole = "+dRole+" and p.ldap_group_name LIKE '"+ldapGroup+"'" ;
		 */
		sqld = "Select  p.idProject from databuck_security_matrix p " + "where p.ldap_group_name LIKE '" + ldapGroup
				+ "'";

		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sqld);
		while (queryForRowSet.next()) {
			dlist.add(queryForRowSet.getInt("idProject"));
		}

		LOG.debug("dlist : " + dlist);

		String strList = dlist.toString();

		strList = strList.replace("[", "").replace("]", "").replace(" ", "");

		LOG.debug("Project list : " + strList);
		String sqldl = "";

		if (!strList.isEmpty()) {

			sqldl = "Select distinct p.idProject, p.projectName,'Y' as isOwner from project p "
					+ " where p.idProject in (" + strList + ")";
		} else {
			sqldl = "Select distinct p.idProject, p.projectName,'Y' as isOwner from project p " + " where 1=0";
		}

		List<Project> projectList = jdbcTemplate.query(sqldl, new RowMapper<Project>() {
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
				Project project = new Project();
				project.setIdProject(rs.getLong("idProject"));
				project.setProjectName(rs.getString("projectName"));
				project.setIsOwner(rs.getString("isOwner"));
				return project;
			}
		});

		/*
		 * List<Domain> DomainList = jdbcTemplate.query(sqldl, new RowMapper<Domain>() {
		 * public Domain mapRow(ResultSet rs, int rowNum) throws SQLException { Domain
		 * domain = new Domain(); domain.setDomainId(rs.getInt("domainId"));
		 * domain.setDomainName(rs.getString("domainName")); return domain; } });
		 */

		return projectList;
	}

	@Override
	public List<Domain> getAllDomains() {
		String sqldl = "";
		sqldl = "Select distinct p.domainId, p.domainName from domain p";

		List<Domain> DomainList = jdbcTemplate.query(sqldl, new RowMapper<Domain>() {
			public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
				Domain domain = new Domain();
				domain.setDomainId(rs.getInt("domainId"));
				domain.setDomainName(rs.getString("domainName"));
				return domain;
			}
		});
		return DomainList;
	}

	@Override
	public List<String> getComponentListofRole(Long idRole) {
		List<String> retList = new ArrayList<String>();
		String query = "select http_url as componentURL from component a, component_access b \n"
				+ " where a.row_id = b.component_row_id \n" + "and b.role_row_id= " + idRole;
		retList = jdbcTemplate.queryForList(query, String.class);
		return retList;
	}

	@Override
	public ArrayList<String> getgroupfrom_Program_Std_Out(String ProgramOutput) {
		ArrayList<String> alist = new ArrayList<String>();
		String[] arrOfStr = ProgramOutput.split("\n");
		for (String a : arrOfStr) { // memberOf:
			if (a.startsWith("memberOf:")) {
				String[] groupname = a.split(":");
				String[] groupname1 = groupname[1].split(",");
				String[] groupname2 = groupname1[0].split("=");
				LOG.debug("group : " + groupname2[1]);
				alist.add(groupname2[1]);
			}
		}
		LOG.debug(alist);
		return alist;
	}

	/* [29-Sep-2020]:Changes for LDAP group role mapping Starts */

	public HashMap<Long, String> getRoleDataFromLdapAfterLogin(ArrayList<String> groupNameList) {
		String groupName = "", query = "";
		HashMap<Long, String> RoleData = new HashMap<Long, String>();

		for (String group : groupNameList) {
			groupName = group;
			query = "select a.roleName as Role, a.idRole as RoleId from Role a, login_group_to_role b, login_group c where c.group_name = '"
					+ groupName + "' and c.row_id = b.login_group_row_id and b.role_row_id = a.idRole";
			LOG.debug("\nRole query: " + query);
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query);

			while (oSqlRowSet.next()) {
				RoleData.put(oSqlRowSet.getLong("RoleId"), oSqlRowSet.getString("Role"));
			}
		}
		return RoleData;
	}

	public HashMap<Long, String> getProjectDataFromLdapAfterLogin(ArrayList<String> groupNameList) {
		String groupName = "", query = "";
		HashMap<Long, String> ProjectData = new HashMap<Long, String>();

		for (String group : groupNameList) {
			groupName = group;
			query = "select a.projectName as Project, a.idProject as ProjectId from project a, login_group_to_project b, login_group c where c.group_name = '"
					+ groupName + "' and c.row_id = b.login_group_row_id and b.project_row_id = a.idProject";
			LOG.debug("\nProject query: " + query);
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query);

			while (oSqlRowSet.next()) {
				ProjectData.put(oSqlRowSet.getLong("ProjectId"), oSqlRowSet.getString("Project"));
			}
		}
		return ProjectData;
	}

	public boolean getIsUserPresent(String cn) {
		boolean flag = false;
		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		String query = "select count(*) as Count from " + user_table + " where firstName = '" + cn + "'";
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query);
		while (oSqlRowSet.next()) {
			if (oSqlRowSet.getInt("Count") > 0) {
				flag = true;
			}
		}
		return flag;
	}

	public boolean insertNewUserRecord(Long idRole, String userName, String password,
			ArrayList<String> aGroupNameList) {
		boolean flag = false;

		int update = userservice.insertDataIntoUserTable(idRole, userName, null, userName, password);
		if (update > 0) {
			flag = true;
			HashMap<Long, String> RoleData = getRoleDataFromLdapAfterLogin(aGroupNameList);
			User user = userDAO.getUserDataByName(userName);
			if (!RoleData.isEmpty()) {
				String query = "insert into UserRole( idUser,idRole )" + "VALUES(?,?)";
				for (Long roleid : RoleData.keySet()) {
					int update2 = jdbcTemplate.update(query, user.getIdUser(), roleid);
					LOG.debug("insert into UserRole=" + update2);
				}
			}
		}
		return flag;
	}

	public List<Project> getProjectListOfUser(ArrayList<String> groupNameList) {
		String groupName = "", query = "";
		List<Project> ProjectData = new ArrayList<Project>();

		for (String group : groupNameList) {
			groupName = group;
			query = "select a.projectName as Project, a.idProject as ProjectId from project a, login_group_to_project b, login_group c where c.group_name = '"
					+ groupName + "' and c.row_id = b.login_group_row_id and b.project_row_id = a.idProject";
			LOG.debug("\nProjectList query: " + query);
			SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(query);

			while (oSqlRowSet.next()) {
				Project p = new Project();
				p.setIdProject(oSqlRowSet.getLong("ProjectId"));
				p.setProjectName(oSqlRowSet.getString("Project"));
				p.setIsOwner("Y");
				ProjectData.add(p);
			}
		}
		return ProjectData;
	}

	public List<Project> getAllDistinctProjectListForUser(HttpSession oSession) {
		List<Project> ProjectData = new ArrayList<Project>();

		String sQuery = "";
		boolean lIsActiveDirectoryAuthentication = JwfSpaInfra
				.getPropertyValue(appDbConnectionProperties, "isActiveDirectoryAuthentication", "xx")
				.equalsIgnoreCase("Y");
		String sUserLDAPGroups, sLdapGroupInClause = "";

		if (lIsActiveDirectoryAuthentication) {
			if (oSession.getAttribute("UserLDAPGroups") != null) {
				sUserLDAPGroups = oSession.getAttribute("UserLDAPGroups").toString();
			} else {
				sUserLDAPGroups = "";
			}
			sLdapGroupInClause = getSqlInClause(sUserLDAPGroups);

			sQuery = "";

			sQuery = sQuery + "select distinct c.idProject as ProjectId, c.projectName as Project ";
			sQuery = sQuery + "from login_group a, Role b, project c, login_group_to_role d, login_group_to_project e ";
			sQuery = sQuery + "where a.row_id = d.login_group_row_id ";
			sQuery = sQuery + "and b.idRole = d.role_row_id ";
			sQuery = sQuery + "and a.row_id = e.login_group_row_id ";
			sQuery = sQuery + "and c.idProject = e.project_row_id ";
			sQuery = sQuery + "and a.group_name in (%1$s) ";

			sQuery = String.format(sQuery, sLdapGroupInClause);
		} else {
			// Query compatibility changes for both POSTGRES and MYSQL
			String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
					: "User";
			sQuery = String.format("select distinct b.idProject as ProjectId, c.projectName as Project from "
					+ user_table
					+ " a, projecttouser b, project c where a.email = b.idUser and   b.idProject = c.idProject and  b.idUser = '%1$s'",
					oSession.getAttribute("email").toString());
		}

		LOG.debug("\nProjectList query: " + sQuery);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sQuery);

		while (oSqlRowSet.next()) {
			Project p = new Project();
			p.setIdProject(oSqlRowSet.getLong("ProjectId"));
			p.setProjectName(oSqlRowSet.getString("Project"));
			p.setIsOwner("Y");
			ProjectData.add(p);
		}

		return ProjectData;
	}

	private String getSqlInClause(String sStringWithCommaDelimitor) {
		String sRetValue = "";
		String sSanitizedValue = sStringWithCommaDelimitor.trim().replaceAll(" ", "");

		if (sSanitizedValue.isEmpty()) {
			sRetValue = "";
		} else {
			String[] aParts = sSanitizedValue.split(",");

			for (int nIndex = 0; nIndex < aParts.length; ++nIndex) {
				aParts[nIndex] = "'" + aParts[nIndex] + "'";
			}
			sRetValue = String.join(",", aParts);
		}

		return sRetValue;
	}

	@Override
	public String generateCSRFToken() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}

	@Override
	public String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		LOG.debug("DATABUCK_HOME:" + databuckHome);
		return databuckHome;
	}

	@Override
	public String getBelongsToRoles(HashMap<Long, String> oMappedRoles) {
		String sRetValue = "";
		List<String> aRoleList = new ArrayList<String>();

		for (Map.Entry<Long, String> oRole : oMappedRoles.entrySet()) {
			aRoleList.add(oRole.getValue().toString());
		}
		sRetValue = String.join(",", aRoleList);

		return sRetValue;
	}

	@Override
	public int checkDaysLeftForLicenseRenewal(HttpSession session) {
		String licenseKey = licenseProperties.getProperty("LicenseKey");
		if (licenseKey != null && !licenseKey.trim().isEmpty()) {
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
			String decryptedLicense = decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[3];
			session.setAttribute("VersionNumber",
					decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[2]);
			SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
			Date licenseExpiryDate = null;
			try {
				licenseExpiryDate = format.parse(decryptedLicense);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (Math.abs(Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()),
					new LocalDate(Calendar.getInstance().getTime())).getDays()) <= 30) {
				session.setAttribute("licenseExpired", "true");
			} else {
				session.setAttribute("licenseExpired", "false");
			}
			session.setAttribute("licenseExpiryDate", licenseExpiryDate);
			return Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()),
					new LocalDate(Calendar.getInstance().getTime())).getDays();
		} else {
			LOG.info("LicenseKey not found !!");
			return 0;
		}
	}

	@Override
	public int validateLicense(Map<String, Object> licenseDetails) {
		String licenseKey = licenseProperties.getProperty("LicenseKey");
		if (licenseKey != null && !licenseKey.trim().isEmpty()) {
			StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
			decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
			String decryptedLicense = decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[3];
			licenseDetails.put("VersionNumber",
					decryptor.decrypt(licenseProperties.getProperty("LicenseKey")).split("-")[2]);
			SimpleDateFormat format = new SimpleDateFormat("MMddyyyy");
			Date licenseExpiryDate = null;
			try {
				licenseExpiryDate = format.parse(decryptedLicense);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int dayDiff = Math.abs(Days.daysBetween(new LocalDate(Calendar.getInstance().getTime()),
					new LocalDate(licenseExpiryDate.getTime())).getDays());
			if (dayDiff <= 30) {
				licenseDetails.put("expiringInMonth", true);
			} else {
				licenseDetails.put("expiringInMonth", false);
			}
			if ((licenseExpiryDate.getTime()  - new Date().getTime()) > 0) {
				licenseDetails.put("licenseExpired", false);
			} else
				licenseDetails.put("licenseExpired", true);
			SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy");
			// details.put("licenseExpiryDate", format2.format(date));
			licenseDetails.put("licenseExpiryDate", format2.format(licenseExpiryDate));
			return Days.daysBetween(new LocalDate(licenseExpiryDate.getTime()),
					new LocalDate(Calendar.getInstance().getTime())).getDays();
		} else {
			LOG.info("LicenseKey not found !!");
			return 0;
		}
	}

	@Override
	public boolean updateLicenseKeyPropertyInDB(String licenseKey) {
		boolean updateStatus = false;
		try {
			// Update the property in database
			String propertyCategoryName = DatabuckPropertyCategory.license.toString();
			String propertyName = "LicenseKey";
			updateStatus = taskDao.updatePropertyValue(propertyCategoryName, propertyName, licenseKey);

			LOG.debug("\n====> Updating property[" + propertyName + "] of PropertyCategory:["
					+ propertyCategoryName + "] - Status[" + updateStatus + "]");

			if (updateStatus) {
				licenseProperties.setProperty(propertyName, licenseKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateStatus;
	}
}
