package com.databuck.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DefectCode;
import com.databuck.bean.Dimension;
import com.databuck.bean.Domain;
import com.databuck.bean.DomainProject;
//import com.databuck.bean.Group;
import com.databuck.bean.Project;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IProjectDAO;
import org.apache.log4j.Logger;

@Repository
public class ProjectDAOImpl implements IProjectDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private Properties appDbConnectionProperties;
	
	private static final Logger LOG = Logger.getLogger(ProjectDAOImpl.class);

	public Project getSelectedProject(Long idProject) {
		String sql = "SELECT idProject, projectName, projectDescription from project Where idProject=" + idProject;
		List<Project> projectList = jdbcTemplate.query(sql, new RowMapper<Project>() {
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
				Project project = new Project();

				project.setIdProject(rs.getLong("idProject"));
				project.setProjectName(rs.getString("projectName"));
				project.setProjectDescription(rs.getString("projectDescription"));

				return project;
			}

		});

		Project project = null;
		if (projectList.size() > 0) {
			return projectList.get(0);
		}
		return project;
	}

	public Long insertDataIntoProjectTable(String projectName, String projectDescription) {
		String sql = "insert into project(projectName, projectDescription, createdAt, updatedAt) "
				+ " VALUES (?,?,now(), now())";

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idproject"
				: "idProject";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, projectName);
				pst.setString(2, projectDescription);
				return pst;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	public Long insertDataIntoDomainTable(String domainName, String projectDescription) {
		String sql = "insert into domain(domainName,is_enterprise_domain) " + " VALUES (?,?)";

		// Query compatibility changes for both POSTGRES and MYSQL
		String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "domainid"
				: "domainId";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
				pst.setString(1, domainName);
				pst.setInt(2, 0);
				return pst;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	public boolean deleteProject(Long idProject) {
		try {
			String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
			if (activeDirectoryFlag == null)
				activeDirectoryFlag = "N";
			LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

			if (activeDirectoryFlag.equals("N")) {

				String sqlUpdate = "DELETE FROM projecttouser WHERE idProject = ?";
				jdbcTemplate.update(sqlUpdate, idProject);
			} else {

				String sqlUpdate = "DELETE FROM login_group_to_project WHERE project_row_id = ?";
				jdbcTemplate.update(sqlUpdate, idProject);

			}
			String sql = "DELETE FROM project WHERE idProject = ?";
			jdbcTemplate.update(sql, idProject);

			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}

	}

	@Override
	public List<Project> getAllProjects() {
		String sql = "SELECT p.idProject, p.projectName, p.projectDescription, dp.domain_id from project as p  "
				+ "left join domain_to_project as dp  on p.idProject= dp.project_id";
		List<Project> projectList = jdbcTemplate.query(sql, new RowMapper<Project>() {
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
				Project project = new Project();

				project.setIdProject(rs.getLong("idProject"));
				project.setProjectName(rs.getString("projectName"));
				project.setProjectDescription(rs.getString("projectDescription"));
				project.setDomainId(rs.getLong("domain_id"));

				return project;
			}

		});
		return projectList;
	}

	@Override
	public List<Domain> getAllDomain() {
		String sql = "SELECT domainId, domainName from domain";
		List<Domain> domainList = jdbcTemplate.query(sql, new RowMapper<Domain>() {
			public Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
				Domain domain = new Domain();

				domain.setDomainId(rs.getInt("domainId"));
				domain.setDomainName(rs.getString("domainName"));
				// domain.setProjectDescription(rs.getString("is_enterprise_domain"));

				return domain;
			}

		});
		return domainList;
	}

	public int insertProjectToGroupAssociation(Long projectId, String groupId, String isOwner) {
		String sql = "insert into projecttouser(idProject, idUser, isOwner) " + " VALUES (?,?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, projectId);
				pst.setString(2, groupId);
				pst.setString(3, isOwner);
				return pst;
			}
		});

		return update;
	}

	@Override
	public int insertProjectToGroupAssociationActive(Long projectId, String groupId, String isOwner) {
		String sql = "insert into projecttoActDiruser(idProject, idUser, isOwner) " + " VALUES (?,?,?)";
		LOG.debug("insert into projecttoActDiruser(idProject, idUser, isOwner) " + " VALUES (" + projectId
				+ "," + groupId + "," + isOwner + ")");
		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, projectId);
				pst.setString(2, groupId);
				pst.setString(3, isOwner);
				return pst;
			}
		});

		return update;
	}

	@Override
	public List<Project> getAllProjectsOfAUser(String idUser) {
		String sql = "";
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		if (activeDirectoryFlag == null)
			activeDirectoryFlag = "N";
		LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		if (activeDirectoryFlag.equals("N")) {
			sql = "Select distinct (p.idProject), p.projectName, pu.isOwner from project p, " + user_table
					+ " u, projecttouser pu" + " where trim(pu.idUser) LIKE trim(u.email) and pu.idProject=p.idProject"
					+ " and trim(u.email) LIKE trim('" + idUser + "')";
		} else {
			sql = "Select distinct (p.idProject), p.projectName, pu.isOwner from project p, projecttoActDiruser pu"
					+ " where pu.idProject=p.idProject" + " and pu.idUser LIKE'" + idUser + "'";

		}

		List<Project> projectList = jdbcTemplate.query(sql, new RowMapper<Project>() {
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
				Project project = new Project();
				project.setIdProject(rs.getLong("idProject"));
				project.setProjectName(rs.getString("projectName"));
				project.setIsOwner(rs.getString("isOwner"));
				return project;
			}
		});

		List<Integer> indexesToBeRemoved = new ArrayList();

		for (int i = 0; i < projectList.size(); i++) {
			for (int j = i + 1; j < projectList.size(); j++) {
				Project firstProject = projectList.get(i);
				Project secondProject = projectList.get(j);
				if (firstProject.getIdProject() == secondProject.getIdProject()) {
					if (firstProject.getIsOwner().equalsIgnoreCase("N")) {
						indexesToBeRemoved.add(i);
					} else if (secondProject.getIsOwner().equalsIgnoreCase("N")) {
						indexesToBeRemoved.add(j);
					}

				}
			}
		}

		for (int i = 0; i < indexesToBeRemoved.size(); i++) {
			int indexToBeRemoved = indexesToBeRemoved.get(i);
			projectList.remove(indexToBeRemoved);
		}
		return projectList;
	}

	public List<Project> getPaginationProject(int start, int numRecords, String sSearch) {
		List<Project> projectlist = new ArrayList<>();

		String searchSQL = "";

		try {

			String sql = "select * from project";

			// Query compatibility changes for both POSTGRES and MYSQL
			String globeSearch = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				globeSearch = " Where idProject::text like '%" + sSearch + "%' or projectName like '%" + sSearch
						+ "%' or projectDescription like '%" + sSearch + "%'";
			else
				globeSearch = " Where idProject like '%" + sSearch + "%' or projectName like '%" + sSearch
						+ "%' or projectDescription like '%" + sSearch + "%'";

			if (sSearch != "") {
				sql = sql + globeSearch;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + start + " LIMIT " + numRecords;
			else
				sql += " limit " + start + ", " + numRecords;

			LOG.debug("Sql :" + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {

				Project project = new Project();
				project.setIdProject(queryForRowSet.getLong("idProject"));
				project.setProjectName(queryForRowSet.getString("projectName"));
				project.setProjectDescription(queryForRowSet.getString("projectDescription"));

				projectlist.add(project);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return projectlist;
	}

	public List<Domain> getPaginationDomain(int start, int numRecords, String sSearch) {
		List<Domain> domainlist = new ArrayList<>();

		String searchSQL = "";

		try {

			String sql = "select * from domain";

			// Query compatibility changes for both POSTGRES and MYSQL
			String globeSearch = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				globeSearch = " Where domainId::text like '%" + sSearch + "%' or domainName like '%" + sSearch + "%'";
			else
				globeSearch = " Where domainId like '%" + sSearch + "%' or domainName like '%" + sSearch + "%'";

			if (sSearch != "") {
				sql = sql + globeSearch;
			}

			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql += " OFFSET " + start + " limit " + numRecords;
			else
				sql += " limit " + start + ", " + numRecords;

			LOG.debug("Sql :" + sql);

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);

			while (queryForRowSet.next()) {

				Domain domain = new Domain();
				domain.setDomainId(queryForRowSet.getInt("domainId"));
				domain.setDomainName(queryForRowSet.getString("domainName"));
				domainlist.add(domain);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return domainlist;
	}

	public int delProjectToGroupAssociation(Long projectId) {
		int update = jdbcTemplate.update("DELETE FROM projecttouser WHERE idProject = " + projectId);

		return update;
	}

	public int delProjectToGroupAssociationActive(Long projectId) {
		int update = jdbcTemplate.update("DELETE FROM projecttoActDiruser WHERE idProject = " + projectId);

		return update;
	}

	public Long updateDataIntoProjectTable(String projectName, String projectDescription, int id) {

		int update = jdbcTemplate.update("UPDATE project " + " SET projectName = '" + projectName + "', "
				+ " projectDescription= '" + projectDescription + "' " + " WHERE idProject= " + id);

		long x = update;

		return x;
	}

	public int getTotalRecordCount() {
		int totalRecords = -1;
		String sql = "select count(*) as count from project";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalRecords = results.getInt("count");
		}
		return totalRecords;
	}

	public int getTotalRecordCountdomain() {
		int totalRecords = -1;
		String sql = "select count(*) as count from domain";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalRecords = results.getInt("count");
		}
		return totalRecords;
	}

	public int getTotalDisplayRecords(String sSearch) {
		int totalDisplayRecords = -1;

		// Query compatibility changes for both POSTGRES and MYSQL
		String sql = "";
		if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
			sql = "select count(*) as count from project Where idProject::text like '%" + sSearch
					+ "%' or projectName like '%" + sSearch + "%' or projectDescription like '%" + sSearch + "%'";
		} else {
			sql = "select count(*) as count from project Where idProject like '%" + sSearch
					+ "%' or projectName like '%" + sSearch + "%' or projectDescription like '%" + sSearch + "%'";
		}
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalDisplayRecords = results.getInt("count");
		}
		return totalDisplayRecords;
	}

	public int getTotalDisplayRecordsdomain(String sSearch) {
		int totalDisplayRecords = -1;
		String sql = "select count(*) as count from domain Where domainId like '%" + sSearch
				+ "%' or domainName like '%" + sSearch + "%'";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if (results.next()) {
			totalDisplayRecords = results.getInt("count");
		}
		return totalDisplayRecords;
	}

	@Override
	public int updateProjectToGroupAssociationActive(Long projectId, String idUser, String isOwner) {

		String sql = "insert into projecttoActDiruser(idProject, idUser, isOwner) " + " VALUES (?,?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, projectId);
				pst.setString(2, idUser);
				pst.setString(3, isOwner);
				return pst;
			}
		});

		return update;

	}

	@Override
	public int updateProjectToGroupAssociation(Long projectId, String idUser, String isOwner) {

		String sql = "insert into projecttouser(idProject, idUser, isOwner) " + " VALUES (?,?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, projectId);
				pst.setString(2, idUser);
				pst.setString(3, isOwner);
				return pst;
			}
		});

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
		try {
			int intProjectId = 0;
			String sql = "SELECT idProject from project where projectName = '" + projectName + "'";
			SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
			while (results.next()) {
				intProjectId = results.getInt("idProject");
			}
			return intProjectId;
		} catch (Exception e) {
			LOG.error("ProjectDAOImpl : getProjectIdByProjectName : Exception ::" + e.getMessage());
			return 0;
		}
	}

	@Override
	public String getProjectNameByProjectid(Long projectId) {
		try {
			String ProjectName = "";
			String sql = "SELECT projectName from project where idProject = '" + projectId + "'";
			SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
			while (results.next()) {
				ProjectName = results.getString("projectName");
			}
			return ProjectName;
		} catch (Exception e) {
			LOG.error("ProjectDAOImpl : getProjectNameByProjectid : Exception ::" + e.getMessage());
			return "";
		}
	}

	@Override
	public Long getProjectIdfromListAppTable(Long idApp) {
		try {
			Long ProjectId = 0l;
			String sql = "SELECT project_id from listApplications where idApp = '" + idApp + "'";
			SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
			while (results.next()) {
				ProjectId = results.getLong("project_id");
			}
			return ProjectId;
		} catch (Exception e) {
			LOG.error("ProjectDAOImpl : getProjectIdfromListAppTable : Exception ::" + e.getMessage());
			return 0l;
		}

	}

	@Override
	public int insertDomainToProjectAssociation(Long domainName, String projectId, String isOwner) {
		String sql = "insert into domaintoproject(domainId, idProject, isOwner) " + " VALUES (?,?,?)";

		int update = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setLong(1, domainName);
				pst.setString(2, projectId);
				pst.setString(3, isOwner);
				return pst;
			}
		});

		return update;
	}

	@Override
	public List<DomainProject> getDomainProjectAssociationOfCurrentUser(List<Project> projlst) {

		List<DomainProject> domainlist = new ArrayList<>();
		String ProjIds_inclause = "";
		List<Long> ProjIds = new ArrayList<Long>();
//		for (Iterator<Project> projIterator = projlst.iterator(); projIterator.hasNext();) {
//			Project project = projIterator.next();
//			ProjIds.add(project.getIdProject());
//		}
		for(Project p:projlst) {
		    ProjIds.add(p.getIdProject());
		}
		ProjIds_inclause = ProjIds.toString().replace("[", "").replace("]", "");
		// if(ProjIds_inclause.isEmpty())
		if (ProjIds.size() == 0) {
			ProjIds_inclause = "-1";// for empty project list ids
		}

		String query = "select a.domainName , b.projectName,a.domainId,b.idProject from domain a ,project b inner join  "
				+ " domain_to_project c on b.idProject = c.project_id where  a.domainId = c.domain_id and b.idProject in ("
				+ ProjIds_inclause + ")";

		SqlRowSet accessControlsData = jdbcTemplate.queryForRowSet(query);
		while (accessControlsData.next()) {

			DomainProject domainl = new DomainProject();
			domainl.setDomainId(accessControlsData.getInt("domainId"));
			domainl.setDomainName(accessControlsData.getString("domainName"));
			domainl.setIdProject(accessControlsData.getInt("idProject"));
			domainl.setProjectName(accessControlsData.getString("projectName"));
			domainlist.add(domainl);
		}
		return domainlist;
	}

	@Override
	public boolean isProjectFromDomain(long projectId, long domainId) {
		boolean projectExits = false;
		String query = "select count(*) from domain_to_project where domain_id=? and project_id=?";
		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class, domainId, projectId);
			if (count > 0)
				projectExits = true;
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching domain_to_project details");
			LOG.error(e.getMessage());
			e.printStackTrace();
			projectExits = false;
		}
		return projectExits;
	}

	@Override
	public boolean isProjectIdValid(long projectId) {
		boolean isProjectIdValid = false;
		String query = "select count(*) from project where idProject=?";
		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class, projectId);
			if (count > 0)
				isProjectIdValid = true;
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching project details");
			LOG.error(e.getMessage());
			e.printStackTrace();
			isProjectIdValid = false;
		}
		return isProjectIdValid;
	}

	@Override
	public boolean isDomainIdValid(long domainId) {
		boolean domainIdValid = false;
		String query = "select count(*) from domain where domainId=?";
		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class, domainId);
			if (count > 0)
				domainIdValid = true;
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching domain details");
			LOG.error(e.getMessage());
			e.printStackTrace();
			domainIdValid = false;
		}
		return domainIdValid;
	}

	@Override
	public List<Project> getAllProjectsForDomain(int domainId) {
		List<Project> projectList = new ArrayList<Project>();
		try {
			String sql = "select p.idProject, p.projectName, p.projectDescription from domain_to_project dp join project p on dp.project_id=p.idProject and dp.domain_id="
					+ domainId;
			projectList = jdbcTemplate.query(sql, new RowMapper<Project>() {
				public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
					Project project = new Project();
					project.setIdProject(rs.getInt("idProject"));
					project.setProjectName(rs.getString("projectName"));
					project.setProjectDescription(rs.getString("projectDescription"));
					return project;
				}

			});
		} catch (Exception e) {
			LOG.error("\n====>Exception occurred while fetching associated Projects of a Domain!!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return projectList;
	}

	@Override
	public List<DomainProject> getAllDomainProjectsForUser(String idUser) {
		List<DomainProject> domainProjectList = new ArrayList<>();
		String sql = "";
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

		// Query compatibility changes for both POSTGRES and MYSQL
		String user_table = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "\"User\""
				: "User";

		if (activeDirectoryFlag != null && activeDirectoryFlag.equals("Y")) {

			sql = "Select distinct p.idProject, p.projectName, pu.isOwner,  dp.domain_id as domainId, d.domainName from project p, projecttoActDiruser pu, domain_to_project dp, domain d  "
					+ "where  pu.idProject=p.idProject and dp.project_id=p.idProject and dp.domain_id=d.domainId and pu.idUser LIKE '"
					+ idUser + "'";

		} else {
			sql = "select distinct p.idProject, p.projectName, pu.isOwner , dp.domain_id as domainId, d.domainName from project p, "
					+ user_table + " u, projecttouser pu, domain_to_project dp, domain d "
					+ " where  pu.idUser LIKE u.email and pu.idProject=p.idProject and dp.project_id=p.idProject and dp.domain_id=d.domainId and u.email LIKE '"
					+ idUser + "'";
		}

		try {
			domainProjectList = jdbcTemplate.query(sql, new RowMapper<DomainProject>() {
				public DomainProject mapRow(ResultSet rs, int rowNum) throws SQLException {
					DomainProject domainProject = new DomainProject();
					domainProject.setDomainId(rs.getInt("domainId"));
					domainProject.setDomainName(rs.getString("domainName"));
					domainProject.setIdProject(rs.getInt("idProject"));
					domainProject.setProjectName(rs.getString("projectName"));
					return domainProject;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return domainProjectList;
	}

	@Override
	public List<DomainProject> getAllDomainProjectsForUser() {
		List<DomainProject> domainProjectList = new ArrayList<>();
		String sql = "";
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		LOG.debug("activeDirectoryFlag-->" + activeDirectoryFlag);

		sql = "Select distinct p.idProject, p.projectName,  dp.domain_id as domainId, d.domainName from project p, "
				+ "domain_to_project dp, domain d where  dp.project_id=p.idProject and dp.domain_id=d.domainId";

		try {
			domainProjectList = jdbcTemplate.query(sql, new RowMapper<DomainProject>() {
				public DomainProject mapRow(ResultSet rs, int rowNum) throws SQLException {
					DomainProject domainProject = new DomainProject();
					domainProject.setDomainId(rs.getInt("domainId"));
					domainProject.setDomainName(rs.getString("domainName"));
					domainProject.setIdProject(rs.getInt("idProject"));
					domainProject.setProjectName(rs.getString("projectName"));
					return domainProject;
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return domainProjectList;
	}

	@Override
	public boolean isDomainProjectValid(int domainId, int projectId) {
		boolean domainIdValid = false;
		String query = "select count(*) from domain_to_project where domain_id=? and project_id=?";
		try {
			int count = jdbcTemplate.queryForObject(query, Integer.class, domainId, projectId);
			if (count > 0)
				domainIdValid = true;
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching domain-project details");
			LOG.error(e.getMessage());
			e.printStackTrace();
			domainIdValid = false;
		}
		return domainIdValid;
	}

	@Override
	public List<Dimension> getAllDimension() {
		List<Dimension> dimensionList = new ArrayList<>();
		String sql = "select idDimension, dimensionName from dimension;";
		try {
			dimensionList = jdbcTemplate.query(sql, new RowMapper<Dimension>() {
				public Dimension mapRow(ResultSet rs, int rowNum) throws SQLException {
					Dimension dimension = new Dimension();
					dimension.setIdDimension(rs.getInt("idDimension"));
					dimension.setDimensionName(rs.getString("dimensionName"));
					return dimension;
				}
			});
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching dimension details");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return dimensionList;
	}

	@Override
	public List<DefectCode> getAllDefectCode() {
		List<DefectCode> defectList = new ArrayList<>();
		String sql = "select dc.defect_code, dc.dimension_id , dc.defect_description, dm.dimensionName "
				+ "from defect_codes dc, dimension dm where dc.dimension_id = dm.idDimension";
		try {
			defectList = jdbcTemplate.query(sql, new RowMapper<DefectCode>() {
				public DefectCode mapRow(ResultSet rs, int rowNum) throws SQLException {
					DefectCode defectCode = new DefectCode();
					defectCode.setDefectCode(rs.getString("defect_code"));
					defectCode.setDefectDescription(rs.getString("defect_description"));
					defectCode.setDimensionId(rs.getInt("dimension_id"));
					defectCode.setDimensionName(rs.getString("dimensionName"));
					return defectCode;
				}
			});
		} catch (Exception e) {
			LOG.error("\n====>Exception occured while fetching dimension details");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return defectList;
	}

	@Override
	public List<Map<String, Object>> getAllProjectsWithAggDomains() {
		List<Map<String, Object>> projectList = new ArrayList<>();

		String projectSql = "select idProject, projectName, projectDescription from project";

		SqlRowSet rs = jdbcTemplate.queryForRowSet(projectSql);
		while (rs.next()) {
			Map<String, Object> project = new HashMap<>();
			project.put("idProject", rs.getLong("idProject"));
			project.put("projectName", rs.getString("projectName"));
			project.put("projectDescription", rs.getString("projectDescription"));

			String domainSql = "select domain_id from domain_to_project where project_id=" + rs.getLong("idProject");
			String domainId = "";

			SqlRowSet rsDomain = jdbcTemplate.queryForRowSet(domainSql);
			while (rsDomain.next()) {
				domainId += rsDomain.getLong("domain_id") + ",";
			}
			if (!domainId.isEmpty()) {
				StringBuffer sb = new StringBuffer(domainId);
				sb.deleteCharAt(sb.length() - 1);
				domainId = sb.toString();
			}
			project.put("domainId", domainId);
			projectList.add(project);
		}
		return projectList;
	}

}
