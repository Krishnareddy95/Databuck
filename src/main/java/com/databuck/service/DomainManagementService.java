package com.databuck.service;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.util.JwfSpaInfra;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

@Service
public class DomainManagementService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(DomainManagementService.class);

	protected static final String GLOBAL_DOMAIN_UNIQUE = "Already one global domain exists and multiple global domains not allowed, save data failed";

	public JSONObject getDomainPageData(String isAngularUICall) throws Exception {
		List<HashMap<String, String>> aDataViewList = null;
		List<HashMap<String, String>> aProjectDataList = null;
		String sDataSql, sDataViewList;
		String[] aColumnSpec = null;
		if (isAngularUICall.equalsIgnoreCase("oldUI")) {
			aColumnSpec = new String[] { "DomainId", "DomainName", "DomainDescription", "DomainId:edit",
					"DomainId:delete", "IsGlobalDomain", "ProjectIds", "ProjectNames" };
		} else {
			aColumnSpec = new String[] { "DomainId", "DomainName", "DomainDescription", "IsGlobalDomain", "ProjectIds",
					"ProjectNames" };
		}

		JSONArray aJsonDataList = new JSONArray();
		JSONObject oJsonRetValue = new JSONObject();
		ObjectMapper oMapper = new ObjectMapper();
		JSONObject oProjectData = new JSONObject();

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

			aDataViewList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, aColumnSpec, "sampleTable",
					null);
			sDataViewList = oMapper.writeValueAsString(aDataViewList);

			aJsonDataList = new JSONArray(sDataViewList);
			oJsonRetValue = oJsonRetValue.put("DataSet", aJsonDataList);

			String sIsEDomainpresentSql = "select domainId, domainName from domain where is_enterprise_domain = 1;";

			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sIsEDomainpresentSql);
			if (queryForRowSet.next()) {
				oJsonRetValue.put("sEDomainPresenntId", "check-" + queryForRowSet.getInt("domainId"));
			}

			sDataSql = "select idProject as projectrowid, projectName as projectname, notification_email as projectemail from project;";
			aProjectDataList = JwfSpaInfra.getDataRowsAsListOfMaps(jdbcTemplate, sDataSql, new String[] {}, "", null);

			for (HashMap<String, String> oProjectRecord : aProjectDataList) {
				JSONObject oProject = new JSONObject();

				oProject.put("ProjectName", oProjectRecord.get("projectname"));
				oProject.put("ProjectEmail", oProjectRecord.get("projectemail"));

				oProjectData.put(oProjectRecord.get("projectrowid"), oProject);
			}
			oJsonRetValue = oJsonRetValue.put("ProjectData", oProjectData);

		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			throw oException;
		}

		return oJsonRetValue;
	}

	public JSONObject addNewDomain(JSONObject oDomainData) {
		String sDuplicateSql = "";
		int nGlobalDomainCount = 0, nNameCount = 0;
		int nGlobalDomain = (oDomainData.getString("IsGlobalDomain").equalsIgnoreCase("Yes")) ? 1 : 0;
		int nNewDomainId = 0;

		String sInsertSql = "insert into domain (domainName, description, is_enterprise_domain) values ('%1$s', '%2$s', %3$s);";
		String sDomainSql = "select max(domainId) as NewDomainId from domain where trim(lower(domainName)) = trim(lower('%1$s'));";
		String[] aProjectIds = (oDomainData.getString("ProjectIds").trim().length() > 0)
				? oDomainData.getString("ProjectIds").split(",")
				: new String[] {};

		SqlRowSet oSqlRowSet = null;

		String sMsg = "New domain added successfully";
		JSONObject oRetValue = new JSONObject();
		boolean lStatus = false;

		try {
			sDuplicateSql = sDuplicateSql + "select \n";
			sDuplicateSql = sDuplicateSql
					+ "case when 1=1 then (select count(*) from domain where is_enterprise_domain = 1) end as GlobalCount, \n";
			sDuplicateSql = sDuplicateSql
					+ "case when 1=1 then (select count(*) from domain where trim(lower(domainName)) = trim(lower('%1$s'))) end as NameCount;";

			oSqlRowSet = jdbcTemplate.queryForRowSet(String.format(sDuplicateSql, oDomainData.getString("DomainName")));
			if (oSqlRowSet.next()) {
				nGlobalDomainCount = oSqlRowSet.getInt("GlobalCount");
				nNameCount = oSqlRowSet.getInt("NameCount");
			}

			if ((nGlobalDomainCount > 0) && (nGlobalDomain > 0)) {
				sMsg = GLOBAL_DOMAIN_UNIQUE;

			} else if (nNameCount > 0) {
				sMsg = String.format(
						"Domain with name as '%1$s' already exists and domain duplicate names not allowed, save data failed",
						oDomainData.getString("DomainName"));
				LOG.error(sMsg);

			} else {
				sInsertSql = String.format(sInsertSql, oDomainData.getString("DomainName"),
						oDomainData.getString("DomainDescription"), nGlobalDomain);
				jdbcTemplate.update(sInsertSql);

				/* Getting newly inserted Domain Id to use as FK for further inserts */
				sDomainSql = String.format(sDomainSql, oDomainData.getString("DomainName"));
				oSqlRowSet = jdbcTemplate.queryForRowSet(sDomainSql);
				nNewDomainId = (oSqlRowSet.next()) ? oSqlRowSet.getInt("NewDomainId") : -1;

				if (nNewDomainId < 0) {
					sMsg = "Error while getting newly inserted domain data, could not link selected projects to domain";
					LOG.error(sMsg);
				} else {
					/* Insert each project id as selected by user as new row each */
					for (String sProjectId : aProjectIds) {
						sInsertSql = String.format(
								"insert into domain_to_project (domain_id, project_id, is_owner) values (%1$s, %2$s, 'Y');",
								nNewDomainId, sProjectId);
						jdbcTemplate.update(sInsertSql);
					}
					lStatus = true;
				}
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			lStatus = false;
			sMsg = String.format("Unexcepted error while saving data \n %1$s", oException.getMessage());
		}

		oRetValue.put("Status", lStatus);
		oRetValue.put("Msg", sMsg);
		oRetValue.put("newDomainId", nNewDomainId);

		return oRetValue;
	}

	public JSONObject updateExistingDomain(JSONObject oDomainData) {
		String deleteSql = "";
		String sUpdateSql = "update domain set is_enterprise_domain = %1$s, description = '%2$s' where domainId = %3$s;";
		String sIsGlobalDomainpresentSql = "select domainId, domainName from domain where is_enterprise_domain = 1;";
		int nGlobalDomain = (oDomainData.getString("IsGlobalDomain").equalsIgnoreCase("Yes")) ? 1 : 0;

		String[] ProjectIds = (oDomainData.getString("ProjectIds").trim().length() > 0)
				? oDomainData.getString("ProjectIds").split(",")
				: new String[] {};

		String sMsg = "Changed domain data saved successfully";
		JSONObject oRetValue = new JSONObject();
		boolean lStatus = false;

		try {
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sIsGlobalDomainpresentSql);
			int domainId = -1;
			while (queryForRowSet.next()) {
				domainId = queryForRowSet.getInt("domainId");
			}
			if (domainId != -1 && domainId != Integer.parseInt(oDomainData.getString("DomainId"))
					&& (nGlobalDomain > 0)) {
				sMsg = GLOBAL_DOMAIN_UNIQUE;
			} else {
				sUpdateSql = String.format(sUpdateSql, nGlobalDomain, oDomainData.getString("DomainDescription"),
						oDomainData.getString("DomainId"));
				jdbcTemplate.update(sUpdateSql);

				/* Delete old mapping */
				deleteSql = deleteSql + "delete from domain_to_project where domain_id = %1$s";
				jdbcTemplate.update(String.format(deleteSql, oDomainData.getString("DomainId")));

				/* Insert each project id as selected by user as new row each */
				for (String sProjectId : ProjectIds) {
					sUpdateSql = String.format(
							"insert into domain_to_project (domain_id, project_id, is_owner) values (%1$s, %2$s, 'Y');",
							oDomainData.getString("DomainId"), sProjectId);
					jdbcTemplate.update(sUpdateSql);
				}
				lStatus = true;
			}
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			lStatus = false;
			sMsg = String.format("Unexcepted error while saving data \n %1$s", oException.getMessage());
		}

		oRetValue.put("Status", lStatus);
		oRetValue.put("Msg", sMsg);

		return oRetValue;
	}

	public JSONObject deleteSelectedDomain(JSONObject oDomainData) {
		String sDeleteSql = "";

		String sMsg = "Domain deleted successfully";
		JSONObject oRetValue = new JSONObject();
		boolean lStatus = false;

		try {
			/* Delete mapping */
			sDeleteSql = String.format("delete from domain_to_project where domain_id = %1$s",
					oDomainData.getString("DomainId"));
			jdbcTemplate.update(sDeleteSql);

			/* Delete domain record */
			sDeleteSql = String.format("delete from domain where domainId = %1$s", oDomainData.getString("DomainId"));
			jdbcTemplate.update(sDeleteSql);

			lStatus = true;
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
			oException.printStackTrace();
			lStatus = false;
			sMsg = String.format("Unexcepted error while deleting data \n %1$s", oException.getMessage());
		}

		oRetValue.put("Status", lStatus);
		oRetValue.put("Msg", sMsg);

		return oRetValue;
	}

}
