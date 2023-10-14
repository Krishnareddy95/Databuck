package com.databuck.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;

import com.databuck.dao.ITaskDAO;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.Dimension;
import com.databuck.bean.DomainLibrary;
import com.databuck.bean.FeaturesAccessControl;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.util.DateUtility;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserSettingService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	IProjectDAO projectDao;

	@Autowired
	IUserDAO iUserDAO;

	@Autowired
	private ITaskDAO iTaskDAO;
	
	private static final Logger LOG = Logger.getLogger(UserSettingService.class);

	public JSONObject getComponentAccessControlAllData(String isAngularUI) {
		JSONObject oJsonRetValue = new JSONObject();
		JSONArray aJsonArray = null;
		LOG.debug("isAngularUI "+isAngularUI);
		List<HashMap<String, String>> aComponentAccessControlList = null;
		ObjectMapper oMapper = new ObjectMapper();
		String[] aColumnSpec = null;

		if (isAngularUI == null) {
			aColumnSpec = new String[] { "Selected", "ComponentRowId", "ComponentTitle", "ModuleRowId", "ModuleName",
					"RoleNames", "RoleRowIds", "ComponentRowId:checkbox", "ComponentRowId:edit" };
		} else {
			aColumnSpec = new String[] { "Selected", "ComponentRowId", "ComponentTitle", "ModuleRowId", "ModuleName",
					"RoleNames", "RoleRowIds" };
		}
		String sSqlQry = "";

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQry = sSqlQry
						+ "select 'false' as Selected, core_qry.ComponentRowId, core_qry.ComponentTitle, core_qry.ModuleRowId, core_qry.ModuleName, string_agg(RoleName,',') as RoleNames, string_agg(RoleRowId::text,',') as RoleRowIds";
				sSqlQry = sSqlQry + " from (";
				sSqlQry = sSqlQry + "	select ";
				sSqlQry = sSqlQry + "		a.idTask as ModuleRowId, a.taskName as ModuleName, ";
				sSqlQry = sSqlQry + "		b.row_id as ComponentRowId, b.component_title as ComponentTitle,";
				sSqlQry = sSqlQry + "		d.idRole as RoleRowId, d.roleName as RoleName";
				sSqlQry = sSqlQry + "	from Module a, component b, component_access c, Role d";
				sSqlQry = sSqlQry + "	where a.idTask = b.module_row_id";
				sSqlQry = sSqlQry + "	and   b.row_id = c.component_row_id";
				sSqlQry = sSqlQry + "	and   c.role_row_id = d.idRole";
				sSqlQry = sSqlQry + ") core_qry";
				sSqlQry = sSqlQry
						+ " group by core_qry.ModuleRowId, core_qry.ModuleName, core_qry.ComponentRowId, core_qry.ComponentTitle;";
			} else {
				sSqlQry = sSqlQry
						+ "select 'false' as Selected, core_qry.ComponentRowId, core_qry.ComponentTitle, core_qry.ModuleRowId, core_qry.ModuleName, group_concat(RoleName) as RoleNames, group_concat(RoleRowId) as RoleRowIds";
				sSqlQry = sSqlQry + " from (";
				sSqlQry = sSqlQry + "	select ";
				sSqlQry = sSqlQry + "		a.idTask as ModuleRowId, a.taskName as ModuleName, ";
				sSqlQry = sSqlQry + "		b.row_id as ComponentRowId, b.component_title as ComponentTitle,";
				sSqlQry = sSqlQry + "		d.idRole as RoleRowId, d.roleName as RoleName";
				sSqlQry = sSqlQry + "	from Module a, component b, component_access c, Role d";
				sSqlQry = sSqlQry + "	where a.idTask = b.module_row_id";
				sSqlQry = sSqlQry + "	and   b.row_id = c.component_row_id";
				sSqlQry = sSqlQry + "	and   c.role_row_id = d.idRole";
				sSqlQry = sSqlQry + ") core_qry";
				sSqlQry = sSqlQry
						+ " group by core_qry.ModuleRowId, core_qry.ModuleName, core_qry.ComponentRowId, core_qry.ComponentTitle;";
			}
			aComponentAccessControlList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sSqlQry, aColumnSpec, "",
					null);

			aJsonArray = new JSONArray(oMapper.writeValueAsString(aComponentAccessControlList));
			oJsonRetValue = oJsonRetValue.put("AccessListData", aJsonArray);

			aJsonArray = new JSONArray(oMapper.writeValueAsString(getRoleListData(isAngularUI)));
			oJsonRetValue = oJsonRetValue.put("RoleListData", aJsonArray);

		} catch (Exception oException) {
			oException.printStackTrace();
		}
		return oJsonRetValue;
	}

	private List<HashMap<String, String>> getRoleListData(String isAngularUI) {
		String sSqlQry = "select 'false' as Selected, idRole as RoleRowId, roleName as RoleName from Role order by upper(roleName);";
		String[] aColumnSpec = null;
		if (isAngularUI == null) {
			aColumnSpec = new String[] { "Selected", "RoleRowId", "RoleName", "RoleRowId:checkbox" };
		} else {
			aColumnSpec = new String[] { "Selected", "RoleRowId", "RoleName" };
		}
		return JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sSqlQry, aColumnSpec, "", null);
	}

	public void saveAccessControl(String[] aSelectedComponents, String[] aSelectedRoles) {
		String sSelectedComponents = String.join(",", aSelectedComponents);
		String sDeleteSql = String.format("delete from component_access where component_row_id in (%1$s)",
				sSelectedComponents);
		String sInsertSql = "";

		jdbcTemplate.batchUpdate(sDeleteSql);

		for (String sComponentRowId : aSelectedComponents) {
			for (String sRoleRowId : aSelectedRoles) {
				sInsertSql = String.format(
						"insert into component_access (component_row_id, role_row_id) values (%1$s, %2$s);",
						sComponentRowId, sRoleRowId);
				DateUtility.DebugLog("SaveAccessControl 01", sInsertSql);
				jdbcTemplate.batchUpdate(sInsertSql);
			}
		}
	}

	public JSONObject getLoginGroupMappingList(String angularUIAPI) throws Exception {
		LOG.debug("angularUIAPI "+angularUIAPI);
		List<HashMap<String, String>> aDataViewList = null;
		String sDataSql, sDataViewList;
		String[] aColumnSpec = null;
		if (angularUIAPI == null) {
			aColumnSpec = new String[] { "GroupRowId", "GroupRowId:checkbox", "GroupName", "IsApproverInt",
					"IsApproverStr", "RoleNames", "RoleRowIds", "ProjectNames", "ProjectRowIds", "GroupRowId:edit",
					"GroupRowId:delete" };
		} else {
			aColumnSpec = new String[] { "GroupRowId", "GroupName", "IsApproverInt",
					"IsApproverStr", "RoleNames", "RoleRowIds", "ProjectNames", "ProjectRowIds" };
		}
		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();
		ObjectMapper oMapper = new ObjectMapper();
		JSONObject oJsonLists = new JSONObject();

		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			sDataSql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sDataSql = sDataSql
						+ "select core_qry.GroupRowId, core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt, \n";
				sDataSql = sDataSql
						+ "		string_agg(distinct Role.idRole::text,',') as RoleRowIds, string_agg(distinct Role.roleName,',') as RoleNames,  \n";
				sDataSql = sDataSql
						+ "		string_agg(distinct project.idProject::text,',') as ProjectRowIds, string_agg(distinct project.projectName,',') as ProjectNames \n";
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
						+ "group by core_qry.GroupRowId, core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt;";
			} else {
				sDataSql = sDataSql
						+ "select core_qry.GroupRowId, core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt, \n";
				sDataSql = sDataSql
						+ "		group_concat(distinct Role.idRole) as RoleRowIds, group_concat(distinct Role.roleName) as RoleNames,  \n";
				sDataSql = sDataSql
						+ "		group_concat(distinct project.idProject) as ProjectRowIds, group_concat(distinct project.projectName) as ProjectNames \n";
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
						+ "group by core_qry.GroupRowId, core_qry.GroupName, core_qry.IsApproverStr, core_qry.IsApproverInt;";
			}
			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonLists = oJsonLists.put("SecurityMatrix", aJsonDataList);

			sDataSql = "";
			sDataSql = sDataSql + "select * from\n";
			sDataSql = sDataSql + "(\n";
			sDataSql = sDataSql + "select idRole as RoleRowId, roleName as RoleName, 'false' as Selected from Role\n";
			sDataSql = sDataSql + ") core_qry;";

			List<Map<String, String>> all_roles_list = jdbcTemplate.query(sDataSql,
					new RowMapper<Map<String, String>>() {

						@Override
						public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
							Map<String, String> role_data = new HashMap<String, String>();
							role_data.put("RoleRowId", String.valueOf(rs.getInt("RoleRowId")));
							role_data.put("RoleName", rs.getString("RoleName"));
							role_data.put("Selected", rs.getString("Selected"));
							return role_data;
						}

					});

			sDataViewList = oMapper.writeValueAsString(all_roles_list);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonLists = oJsonLists.put("AllRoles", aJsonDataList);

			sDataSql = "";
			sDataSql = sDataSql + "select * from\n";
			sDataSql = sDataSql + "(\n";
			sDataSql = sDataSql
					+ "select idProject as ProjectRowId, projectName as ProjectName, 'false' as Selected from project\n";
			sDataSql = sDataSql + ") core_qry;";

			List<Map<String, String>> all_projects_list = jdbcTemplate.query(sDataSql,
					new RowMapper<Map<String, String>>() {

						@Override
						public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
							Map<String, String> role_data = new HashMap<String, String>();
							role_data.put("ProjectRowId", String.valueOf(rs.getInt("ProjectRowId")));
							role_data.put("ProjectName", rs.getString("ProjectName"));
							role_data.put("Selected", rs.getString("Selected"));
							return role_data;
						}

					});

			sDataViewList = oMapper.writeValueAsString(all_projects_list);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonLists = oJsonLists.put("AllProjects", aJsonDataList);
			oJsonRetValue = oJsonRetValue.put("DataSet", oJsonLists);

		} catch (Exception oException) {
			LOG.error("exception "+oException.getMessage());
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}
	
	public boolean isLoginLdapGroupExists(String sLoginGroupName) {
		LOG.debug("sLoginGroupName "+sLoginGroupName);
		boolean lIsDeleteGroupCall = (sLoginGroupName.indexOf("-2") == 0) ? true : false;
		boolean lRetValue = false;
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		LOG.debug("\nActiveDirectoryFlag: " + activeDirectoryFlag);

		if (activeDirectoryFlag.equals("N")) {
			lRetValue = true;
		} else if (lIsDeleteGroupCall) {
			lRetValue = true;
		} else {
			// This logic is not correct, hence commenting out
			// New logic will be written in future release
			// lRetValue = jwfSpaInfra.IsLoginLdapGroupExistsOnLdap(sLoginGroupName);
			lRetValue = true;
		}
		return lRetValue;
	}

	public String saveLoginGroupAndRoleProjectMapping(JSONObject oDataToSave,Long idUser,String userName) {
		LOG.debug("oDataToSave "+oDataToSave);
		String[] aSelectedRoles = oDataToSave.getString("Roles").split(",", 0);
		String[] aSelectedProjects = oDataToSave.getString("Projects").split(",", 0);

		String sDeleteSql, sInsertSql = "", sUpdateSql, sMsg = "";
		String sGroupName = oDataToSave.getString("GroupName").trim();
		boolean lIsDeleteGroupCall = ((sGroupName.indexOf("-2") == 0) ? true : false);

		DateUtility.DebugLog("SaveLoginGroupAndRoleProjectMapping 01", String
				.format("Data to Save is '%1$s', '%2$s', '%3$s' ", oDataToSave, aSelectedRoles, aSelectedProjects));

		sDeleteSql = String.format("delete from login_group_to_role where login_group_row_id = %1$s",
				oDataToSave.getString("GroupRowId"));
		jdbcTemplate.batchUpdate(sDeleteSql);

		sDeleteSql = String.format("delete from login_group_to_project where login_group_row_id = %1$s",
				oDataToSave.getString("GroupRowId"));
		jdbcTemplate.batchUpdate(sDeleteSql);
		// changes regarding Audit trail
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		/* GroupRowId = -1 means new group. GroupName = -2 means delete. */
		if (Integer.valueOf(oDataToSave.getString("GroupRowId")) < 0) {
			sInsertSql = String.format("insert into login_group (group_name,is_approver) values ('%1$s', %2$s);",
					oDataToSave.getString("GroupName"), oDataToSave.getString("IsApproverInt"));

			KeyHolder keyHolder = new GeneratedKeyHolder();
			String finalSSqlToUpdate = sInsertSql;
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(finalSSqlToUpdate, new String[] { "row_id" });
					return pst;
				}
			}, keyHolder);

			Long row_id = keyHolder.getKey().longValue();
			oDataToSave.put("GroupRowId", String.valueOf(getGroupRowId(oDataToSave.getString("GroupName"))));
			iTaskDAO.addAuditTrailDetail(idUser, userName,
					DatabuckConstants.DBK_FEATURE_LOGIN_GROUP_MAPPING, formatter.format(new Date()), row_id,
					DatabuckConstants.ACTIVITY_TYPE_CREATED,oDataToSave.getString("GroupName"));
		} else {
			sUpdateSql = String.format("update login_group set is_approver = %1$s where row_id = %2$s",
					oDataToSave.getString("IsApproverInt"), oDataToSave.getString("GroupRowId"));
			jdbcTemplate.batchUpdate(sUpdateSql);
			if(sGroupName!=null && !sGroupName.trim().isEmpty()&& !sGroupName.equalsIgnoreCase("-2")){
				iTaskDAO.addAuditTrailDetail(idUser, userName,
						DatabuckConstants.DBK_FEATURE_LOGIN_GROUP_MAPPING, formatter.format(new Date()), Long.valueOf(oDataToSave.getString("GroupRowId")),
						DatabuckConstants.ACTIVITY_TYPE_EDITED,oDataToSave.getString("GroupName"));
			}
		}

		DateUtility.DebugLog("SaveLoginGroupAndRoleProjectMapping 02", String.format("Data to Save is '%1$s', '%2$s'",
				lIsDeleteGroupCall, oDataToSave.getString("GroupName")));

		if (lIsDeleteGroupCall) {
			iTaskDAO.addAuditTrailDetail(idUser, userName,
					DatabuckConstants.DBK_FEATURE_LOGIN_GROUP_MAPPING, formatter.format(new Date()), Long.valueOf(oDataToSave.getString("GroupRowId")),
					DatabuckConstants.ACTIVITY_TYPE_DELETED,oDataToSave.getString("GroupName"));
			deleteGroup(oDataToSave.getString("GroupRowId"));
			sMsg = "Selected Group deleted successfully";

		} else {

			for (String sRoleRowId : aSelectedRoles) {
				sInsertSql = String.format(
						"insert into login_group_to_role (login_group_row_id, role_row_id) values (%1$s, %2$s);",
						oDataToSave.getString("GroupRowId"), sRoleRowId);
				jdbcTemplate.batchUpdate(sInsertSql);
			}

			for (String sProjectRowId : aSelectedProjects) {
				sInsertSql = String.format(
						"insert into login_group_to_project (login_group_row_id, project_row_id) values (%1$s, %2$s);",
						oDataToSave.getString("GroupRowId"), sProjectRowId);
				jdbcTemplate.batchUpdate(sInsertSql);
			}
			sMsg = "Role(s) and Project(s) assignment successfully saved";
		}

		return sMsg;
	}

	private void deleteGroup(String sGroupRowId) {
		String sDeleteSql = String.format("delete from login_group_to_role where login_group_row_id = %1$s",
				sGroupRowId);

		DateUtility.DebugLog("deleteGroup 01",
				String.format("Row Id to Delete is '%1$s'\n%2$s", sGroupRowId, sDeleteSql));

		jdbcTemplate.batchUpdate(sDeleteSql);

		sDeleteSql = String.format("delete from login_group_to_project where login_group_row_id = %1$s", sGroupRowId);
		jdbcTemplate.batchUpdate(sDeleteSql);

		sDeleteSql = String.format("delete from login_group where row_id = %1$s", sGroupRowId);
		jdbcTemplate.batchUpdate(sDeleteSql);
	}

	private int getGroupRowId(String sGroupName) {
		String sQuery = String.format("select row_id from login_group where group_name = '%1$s'", sGroupName);
		SqlRowSet oSqlRowSet = jdbcTemplate.queryForRowSet(sQuery);
		int nRetValue = 0;

		if (oSqlRowSet.next()) {
			nRetValue = oSqlRowSet.getInt("row_id");
		}

		return nRetValue;
	}
	
	public List<FeaturesAccessControl> getComponentAccessControlData() {

		String sSqlQry = "";
		List<FeaturesAccessControl> list = new ArrayList<FeaturesAccessControl>();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sSqlQry = sSqlQry
						+ "select 'false' as Selected, core_qry.ComponentRowId, core_qry.ComponentTitle, core_qry.ModuleRowId, core_qry.ModuleName, string_agg(RoleName,',') as RoleNames, string_agg(RoleRowId::text,',') as RoleRowIds";
				sSqlQry = sSqlQry + " from (";
				sSqlQry = sSqlQry + "	select ";
				sSqlQry = sSqlQry + "		a.idTask as ModuleRowId, a.taskName as ModuleName, ";
				sSqlQry = sSqlQry + "		b.row_id as ComponentRowId, b.component_title as ComponentTitle,";
				sSqlQry = sSqlQry + "		d.idRole as RoleRowId, d.roleName as RoleName";
				sSqlQry = sSqlQry + "	from Module a, component b, component_access c, Role d";
				sSqlQry = sSqlQry + "	where a.idTask = b.module_row_id";
				sSqlQry = sSqlQry + "	and   b.row_id = c.component_row_id";
				sSqlQry = sSqlQry + "	and   c.role_row_id = d.idRole";
				sSqlQry = sSqlQry + ") core_qry";
				sSqlQry = sSqlQry
						+ " group by core_qry.ModuleRowId, core_qry.ModuleName, core_qry.ComponentRowId, core_qry.ComponentTitle;";
			} else {
				sSqlQry = sSqlQry
						+ "select 'false' as Selected, core_qry.ComponentRowId, core_qry.ComponentTitle, core_qry.ModuleRowId, core_qry.ModuleName, group_concat(RoleName) as RoleNames, group_concat(RoleRowId) as RoleRowIds";
				sSqlQry = sSqlQry + " from (";
				sSqlQry = sSqlQry + "	select ";
				sSqlQry = sSqlQry + "		a.idTask as ModuleRowId, a.taskName as ModuleName, ";
				sSqlQry = sSqlQry + "		b.row_id as ComponentRowId, b.component_title as ComponentTitle,";
				sSqlQry = sSqlQry + "		d.idRole as RoleRowId, d.roleName as RoleName";
				sSqlQry = sSqlQry + "	from Module a, component b, component_access c, Role d";
				sSqlQry = sSqlQry + "	where a.idTask = b.module_row_id";
				sSqlQry = sSqlQry + "	and   b.row_id = c.component_row_id";
				sSqlQry = sSqlQry + "	and   c.role_row_id = d.idRole";
				sSqlQry = sSqlQry + ") core_qry";
				sSqlQry = sSqlQry
						+ " group by core_qry.ModuleRowId, core_qry.ModuleName, core_qry.ComponentRowId, core_qry.ComponentTitle;";
			}
			SqlRowSet componentAccessControlData = jdbcTemplate.queryForRowSet(sSqlQry);

			while (componentAccessControlData.next()) {
				String moduleName = componentAccessControlData.getString("modulename");
				String roleNames = componentAccessControlData.getString("rolenames");
				String components = componentAccessControlData.getString("componenttitle");
				FeaturesAccessControl featureAccessControl = new FeaturesAccessControl(moduleName, components,
						roleNames);
				list.add(featureAccessControl);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	public List<DomainLibrary> getDomainManagementServiceData() {

		String sDataSql = "";
		List<DomainLibrary> list = new ArrayList<DomainLibrary>();
		try {
				// Query compatibility changes for both POSTGRES and MYSQL
				if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
					sDataSql = "";
					sDataSql = sDataSql + "select core_qry.*, sub_qry.ProjectIds, sub_qry.ProjectNames from ";
					sDataSql = sDataSql + "( ";
					sDataSql = sDataSql
							+ "select domainId as DomainId, domainName as DomainName, description as DomainDescription, CASE  WHEN is_enterprise_domain >0 THEN 'Yes' ELSE 'No' END as IsGlobalDomain from domain ";
					sDataSql = sDataSql + ") as core_qry ";
					sDataSql = sDataSql + "left outer join (";
					sDataSql = sDataSql
							+ "select  a. domain_id as DomainId, string_agg(a.project_id::text,',') as ProjectIds, string_agg(b.projectName,',') as ProjectNames ";
					sDataSql = sDataSql + "from domain_to_project a, project b ";
					sDataSql = sDataSql + "where a.project_id = b.idProject ";
					sDataSql = sDataSql + "group by a.domain_id ";
					sDataSql = sDataSql + ") as sub_qry on core_qry.DomainId = sub_qry.DomainId;";
				} else {
					sDataSql = "";
					sDataSql = sDataSql + "select core_qry.*, sub_qry.ProjectIds, sub_qry.ProjectNames from ";
					sDataSql = sDataSql + "( ";
					sDataSql = sDataSql
							+ "select domainId as DomainId, domainName as DomainName, description as DomainDescription, CASE  WHEN is_enterprise_domain >0 THEN 'Yes' ELSE 'No' END as IsGlobalDomain from domain ";
					sDataSql = sDataSql + ") as core_qry ";
					sDataSql = sDataSql + "left outer join (";
					sDataSql = sDataSql
							+ "select  a. domain_id as DomainId, group_concat(a.project_id) as ProjectIds, group_concat(b.projectName) as ProjectNames ";
					sDataSql = sDataSql + "from domain_to_project a, project b ";
					sDataSql = sDataSql + "where a.project_id = b.idProject ";
					sDataSql = sDataSql + "group by a.domain_id ";
					sDataSql = sDataSql + ") as sub_qry on core_qry.DomainId = sub_qry.DomainId;";
				}
			SqlRowSet domainData = jdbcTemplate.queryForRowSet(sDataSql);

			while (domainData.next()) {
				int domainId = domainData.getInt("domainid");
				String domainName = domainData.getString("domainname");
				String isGlobalDomain = domainData.getString("isglobaldomain");
				String projectids = domainData.getString("projectids");
				String projectname = domainData.getString("projectnames");
				DomainLibrary domainLibraryData = new DomainLibrary(domainId, domainName,
						isGlobalDomain,projectids,projectname);
				list.add(domainLibraryData);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	public JSONObject createDefectCodesByFile(List<String[]> lines) {
		JSONObject json = new JSONObject();
		LOG.debug("lines "+lines);
		String status = "failed";
		String message = "";
		JSONObject result = new JSONObject();
		JSONArray duplicateRecordsData = new JSONArray();
		JSONArray failedRecordsData = new JSONArray();
		int insertedRecordCount = 0;
		int duplicateRecordCount = 0;
		int failedRecordCount = 0 ,totalCount=lines.size()-1;
		String htmlTagPattern = ".*\\<[^>]+>.*";

		try  {
			String[] header = lines.get(0);
			if (header.length == 3 && lines.size() >= 2) {
				List<Dimension> dimensionList = projectDao.getAllDimension();
				try {
					for (int i = 1; i < lines.size(); i++) {
						String defectCode = "";
						String defectDescription = "";
						String dimensionName;
						String[] values = lines.get(i);
						if(String.join("", values).trim().equals(""))
						{
							 continue;
						}
						int dimensionId = 0;
						if (values.length > 0) {

							defectCode = values[0].trim();
							defectDescription = values[1].trim();
							dimensionName = values[2].trim();
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("defectCode", defectCode);
							jsonObject.put("dimensionName", dimensionName);

							if(defectCode.matches(htmlTagPattern) || defectDescription.matches(htmlTagPattern) || dimensionName.matches(htmlTagPattern)){
								failedRecordsData.put(jsonObject);
								failedRecordCount++;
								continue;
							}

							if((defectCode != null && !defectCode.isEmpty()) && ((dimensionName != null && !dimensionName.isEmpty()))) {
								List<Dimension> collect = dimensionList.stream().filter(dimension -> dimension.getDimensionName().equals(dimensionName)).collect(Collectors.toList());
								if (collect.size() > 0 ) {
									dimensionId = collect.get(0).getIdDimension();
									boolean isCombinationExist = iUserDAO.getDefectCodeAndDiamensionId(defectCode, dimensionId);
									if (isCombinationExist) {
										duplicateRecordsData.put(jsonObject);
										duplicateRecordCount++;
									} else {
										int addDefectCode = iUserDAO.addDefectCode(defectCode, defectDescription, dimensionId);
										if (addDefectCode > 0) {
											insertedRecordCount++;
										}
									}
								} else {
									failedRecordsData.put(jsonObject);
									failedRecordCount++;
								}
							} else {
								failedRecordsData.put(jsonObject);
								failedRecordCount++;
							}
						}
					}
					result.put("totalCount", totalCount);
					result.put("failedRecordCount", failedRecordCount);
					result.put("failedRecordsData", failedRecordsData);
					result.put("duplicateRecordCount", duplicateRecordCount);
					result.put("duplicateRecordsData", duplicateRecordsData);
					message="success";
					status="success";
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Invalid row");
				}
			}  else {
				message = "Unable to process this file!! Does not contains header!!";
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			throw new RuntimeException(e);
		}
		json.put("result", result);
		json.put("message", message);
		json.put("status", status);
		return json;
	}
	
}
