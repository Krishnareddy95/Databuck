package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DbkFMConnectionSummary;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMSummaryDetails;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DBKFileMonitoringDao;

@Repository
public class DBKFileMonitoringDaoImpl implements DBKFileMonitoringDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;
	
	private static final Logger LOG = Logger.getLogger(DBKFileMonitoringDaoImpl.class);

	public List<DbkFMSummaryDetails> getDBKFMSummaryDetails(Long idApp) {
		
		LOG.debug("idApp "+idApp);

		List<DbkFMSummaryDetails> fmSummaryDetailsList = null;
		try {
			String sql = "select * from dbk_fm_summary_details where load_date=(select MAX(load_date) from dbk_fm_summary_details  where  validation_id  = "
					+ idApp + ")  And validation_id  = " + idApp;
			
			LOG.debug("sql dbk_fm_summary_details = " + sql);
			
			fmSummaryDetailsList = jdbcTemplate1.query(sql, new RowMapper<DbkFMSummaryDetails>() {
				@Override
				public DbkFMSummaryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					DbkFMSummaryDetails dbkFMSummaryDetails = new DbkFMSummaryDetails();

					long connectionId = rs.getLong("connection_id");

					String connectionName = "";
					try {
						// Get Connection Name
						String con_sql = "Select schemaName from listDataSchema where idDataSchema=?";
						connectionName = jdbcTemplate.queryForObject(con_sql, String.class, connectionId);
					} catch (Exception e) {
						LOG.error("exception "+e.getMessage());
						e.printStackTrace();
					}

					dbkFMSummaryDetails.setConnection_id(connectionId);
					dbkFMSummaryDetails.setConnectionName(connectionName);
					dbkFMSummaryDetails.setValidation_id(idApp);
					dbkFMSummaryDetails.setSchema_name(rs.getString("schema_name"));
					dbkFMSummaryDetails.setTable_or_subfolder_name(rs.getString("table_or_subfolder_name"));
					dbkFMSummaryDetails.setFile_indicator(rs.getString("file_indicator"));
					dbkFMSummaryDetails.setDayOfWeek(rs.getString("dayOfWeek"));
					dbkFMSummaryDetails.setLoad_date(rs.getString("load_date"));
					dbkFMSummaryDetails.setLoaded_hour(rs.getInt("loaded_hour"));
					dbkFMSummaryDetails.setExpected_minute(rs.getInt("expected_minute"));
					dbkFMSummaryDetails.setActual_file_count(rs.getInt("actual_file_count"));
					dbkFMSummaryDetails.setExpected_file_count(rs.getInt("expected_file_count"));
					dbkFMSummaryDetails.setStatus(rs.getString("status"));

					return dbkFMSummaryDetails;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			if (fmSummaryDetailsList == null)
				fmSummaryDetailsList = new ArrayList<>();
		}
		return fmSummaryDetailsList;
	}
	
	/*
	 * This returns the list of connections with fileMonitoringType - 'snowflake', 'azuredatalakestoragegen2batch', 'aws s3'
	 */
	public List<DbkFMConnectionSummary> getFileMonitoringValidationsList(long domainId,long projectId) {

		List<DbkFMConnectionSummary> fmConnectionSummaryList = null;
		try {
			String conSql = "select t3.idApp, t3.idDataSchema from (select t1.idApp, t2.idDataSchema from "
					+ "(select idApp, name from listApplications where appType='File Monitoring' and fileMonitoringType in ('snowflake', 'azuredatalakestoragegen2batch', 'aws s3','databricksdeltalake') and active='yes') t1 "
					+ "join fm_connection_details t2 on t1.idApp=t2.idApp) t3 join listDataSchema lds on t3.idDataSchema=lds.idDataSchema where lds.project_id=? and lds.domain_id=? and action='Yes'";
			
			LOG.debug("conSql "+conSql);
			List<Map<String,Object>> conDetailsList = jdbcTemplate.queryForList(conSql,projectId,domainId);
			
			if(conDetailsList != null && conDetailsList.size() > 0) {
				
				List<Long> connectionIdList = new ArrayList<Long>();
				List<Long> validationIdList = new ArrayList<Long>();
				
				for(Map<String,Object> conMap : conDetailsList) {
					try {
						Long connectionId = Long.parseLong(conMap.get("idDataSchema").toString());
						Long validationId = Long.parseLong(conMap.get("idApp").toString());
						
						connectionIdList.add(connectionId);
						validationIdList.add(validationId);
						
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				if(connectionIdList.size()>0 && validationIdList.size()>0) {
					String connectionIdsStr = connectionIdList.toString().replace("[", "").replace("]", "");
					String validationIdsStr = validationIdList.toString().replace("[", "").replace("]", "");
					
					String sql = "select fs.connection_id, fs.validation_id, fs.load_date, sum(fs.expected_file_count) AS expectedFileCount, sum(fs.actual_file_count) "
							+ " AS actualFileCount,  sum(case when(fs.actual_file_count>fs.expected_file_count and status != 'new file') then (fs.actual_file_count - fs.expected_file_count) else 0 end) AS duplicateFileCount, sum(case when(fs.status = 'new file') then fs.actual_file_count else 0 end) AS newFileCount, sum(case when(fs.expected_file_count=0 or "
							+ " fs.actual_file_count>fs.expected_file_count) then 0 else (fs.expected_file_count - fs.actual_file_count) end) AS missingFileCount,case when (sum(case when(fs.expected_file_count=0 or " +
							" fs.actual_file_count>fs.expected_file_count) then 0 else (fs.expected_file_count - fs.actual_file_count) end) >0)then 'failed' else 'passed' end AS ingestionStatus from dbk_fm_summary_details fs join "
							+ " (select connection_id, validation_id, max(load_date) as load_date from dbk_fm_summary_details group by connection_id, validation_id) t1 on fs.connection_id=t1.connection_id "
							+ " and fs.validation_id=t1.validation_id and fs.load_date=t1.load_date where fs.connection_id in ("+connectionIdsStr+") and fs.validation_id in ("+validationIdsStr+")  group by fs.connection_id, fs.validation_id, fs.load_date";
					
					fmConnectionSummaryList = jdbcTemplate1.query(sql, new RowMapper<DbkFMConnectionSummary>() {
						@Override
						public DbkFMConnectionSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
							DbkFMConnectionSummary dbkFMSummary = new DbkFMConnectionSummary();
		
							long connectionId = rs.getLong("connection_id");
							long validationId = rs.getLong("validation_id");
							
							String connectionName = "";
							String connectionType = "";
							try {
								// Get Connection Name
								String con_sql = "Select schemaName,schemaType from listDataSchema where idDataSchema=?";
								Map<String,Object> conDetailsMap = jdbcTemplate.queryForMap(con_sql, connectionId);
								connectionName = (String) conDetailsMap.get("schemaName");
								connectionType = (String) conDetailsMap.get("schemaType");
							} catch (Exception e) {
								LOG.error("exception "+e.getMessage());
								e.printStackTrace();
							}
							
							String validationName = "";
							try {
								// Get Connection Name
								String val_sql = "Select name from listApplications where idApp=?";
								validationName = jdbcTemplate.queryForObject(val_sql, String.class, validationId);
							} catch (Exception e) {
								LOG.error("exception "+e.getMessage());
								e.printStackTrace();
							}
		
							dbkFMSummary.setConnectionId(connectionId);
							dbkFMSummary.setConnectionName(connectionName);
							dbkFMSummary.setConnectionType(connectionType);
							dbkFMSummary.setValidationId(validationId);
							dbkFMSummary.setValidationName(validationName);
							dbkFMSummary.setExecutionDate(rs.getString("load_date"));
							dbkFMSummary.setExpectedFileCount(rs.getInt("expectedFileCount"));
							dbkFMSummary.setArrivedFileCount(rs.getInt("actualFileCount"));
							dbkFMSummary.setDuplicateFileCount(rs.getInt("duplicateFileCount"));
							dbkFMSummary.setNewFileCount(rs.getInt("newFileCount"));
							dbkFMSummary.setMissingFileCount(rs.getInt("missingFileCount"));
							dbkFMSummary.setIngestionStatus(rs.getString("ingestionStatus"));
		
							return dbkFMSummary;
						}
					});
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			if (fmConnectionSummaryList == null)
				fmConnectionSummaryList = new ArrayList<>();
		}
		return fmConnectionSummaryList;
	}

	public List<DbkFMSummaryDetails> getDBKFMSummaryDetailsForConnection(Long idApp, Long ConnectionId, String fromDate,
			String toDate) {

		List<DbkFMSummaryDetails> fmSummaryDetailsList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
				sql = "select * from dbk_fm_summary_details where validation_id=" + idApp + " and connection_id="
						+ ConnectionId + " and status in ('missing', 'new file', 'additional') and load_date BETWEEN '"
						+ fromDate + "'::Date AND '" + toDate
						+ "'::Date union (select * from dbk_fm_summary_details where validation_id=" + idApp + ""
						+ " and connection_id=" + ConnectionId + " and status in ('ready') and load_date BETWEEN '"
						+ fromDate + "'::Date " + "AND '" + toDate
						+ "'::Date  and loaded_hour in (select max(loaded_hour) from dbk_fm_summary_details where validation_id="
						+ idApp + " and connection_id=" + ConnectionId
						+ " and status in ('ready') and load_date BETWEEN '" + fromDate + "'::Date AND '" + toDate
						+ "'::Date and status in ('ready') group by load_date,connection_id,validation_id,schema_name,table_or_subfolder_name ))";
			else
				sql = "select * from dbk_fm_summary_details where validation_id=" + idApp + " and connection_id="
						+ ConnectionId + " and status in ('missing', 'new file', 'additional') and load_date BETWEEN '"
						+ fromDate + "' AND '" + toDate
						+ "' union (select * from dbk_fm_summary_details where validation_id=" + idApp + ""
						+ " and connection_id=" + ConnectionId + " and status in ('ready') and load_date BETWEEN '"
						+ fromDate + "' " + "AND '" + toDate
						+ "'  and loaded_hour in (select max(loaded_hour) from dbk_fm_summary_details where validation_id="
						+ idApp + " and connection_id=" + ConnectionId
						+ " and status in ('ready') and load_date BETWEEN '" + fromDate + "' AND '" + toDate
						+ "' and status in ('ready') group by load_date,connection_id,validation_id,schema_name,table_or_subfolder_name ))";
			
			LOG.debug("sql "+sql);

			fmSummaryDetailsList = jdbcTemplate1.query(sql, new RowMapper<DbkFMSummaryDetails>() {
				@Override
				public DbkFMSummaryDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					DbkFMSummaryDetails dbkFMSummaryDetails = new DbkFMSummaryDetails();

					long connectionId = rs.getLong("connection_id");

					String connectionName = "";
					String connectionType = "";
					try {
						// Get Connection Name
						String con_sql = "Select schemaName,schemaType from listDataSchema where idDataSchema=?";
						Map<String, Object> conDetailsMap = jdbcTemplate.queryForMap(con_sql, connectionId);
						connectionName = (String) conDetailsMap.get("schemaName");
						connectionType = (String) conDetailsMap.get("schemaType");
					} catch (Exception e) {
						LOG.error("exception "+e.getMessage());
						e.printStackTrace();
					}

					dbkFMSummaryDetails.setConnection_id(connectionId);
					dbkFMSummaryDetails.setConnectionName(connectionName);
					dbkFMSummaryDetails.setConnectionType(connectionType);
					dbkFMSummaryDetails.setValidation_id(idApp);
					dbkFMSummaryDetails.setSchema_name(rs.getString("schema_name"));
					dbkFMSummaryDetails.setTable_or_subfolder_name(rs.getString("table_or_subfolder_name"));
					dbkFMSummaryDetails.setFile_indicator(rs.getString("file_indicator"));
					dbkFMSummaryDetails.setDayOfWeek(rs.getString("dayOfWeek"));
					dbkFMSummaryDetails.setLoad_date(rs.getString("load_date"));
					dbkFMSummaryDetails.setLoaded_hour(rs.getInt("loaded_hour"));
					dbkFMSummaryDetails.setExpected_minute(rs.getInt("expected_minute"));
					dbkFMSummaryDetails.setActual_file_count(rs.getInt("actual_file_count"));
					dbkFMSummaryDetails.setExpected_file_count(rs.getInt("expected_file_count"));
					dbkFMSummaryDetails.setStatus(rs.getString("status"));

					return dbkFMSummaryDetails;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			if (fmSummaryDetailsList == null)
				fmSummaryDetailsList = new ArrayList<>();
		}
		return fmSummaryDetailsList;
	}
	@Override
	public List<DbkFMFileArrivalDetails> getDBKFMFileArrivalDetails(Long idApp) {

		LOG.debug("idApp "+idApp);
		List<DbkFMFileArrivalDetails> dbkFMFileArrivalList = null;
		try {
			String sql = "select * from dbk_fm_filearrival_details where load_date=(select MAX(load_date) from dbk_fm_filearrival_details where  validation_id  = "
					+ idApp + " ) And  validation_id  = " + idApp;

			
			LOG.debug("sql "+sql);
			dbkFMFileArrivalList = jdbcTemplate1.query(sql, new RowMapper<DbkFMFileArrivalDetails>() {
				@Override
				public DbkFMFileArrivalDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					DbkFMFileArrivalDetails dbkFMFileArrivalList = new DbkFMFileArrivalDetails();

					long connectionId = rs.getLong("connection_id");

					String connectionName = "";
					try {
						// Get Connection Name
						String con_sql = "Select schemaName from listDataSchema where idDataSchema=?";
						connectionName = jdbcTemplate.queryForObject(con_sql, String.class, connectionId);
					} catch (Exception e) {
						LOG.error("exception "+e.getMessage());
						e.printStackTrace();
						
					}

					dbkFMFileArrivalList.setConnection_id(connectionId);
					dbkFMFileArrivalList.setConnectionName(connectionName);
					dbkFMFileArrivalList.setValidation_id(idApp);
					dbkFMFileArrivalList.setSchema_name(rs.getString("schema_name"));
					dbkFMFileArrivalList.setTable_or_subfolder_name(rs.getString("table_or_subfolder_name"));
					dbkFMFileArrivalList.setFile_indicator(rs.getString("file_indicator"));
					dbkFMFileArrivalList.setDayOfWeek(rs.getString("dayOfWeek"));
					dbkFMFileArrivalList.setLoad_date(rs.getString("load_date"));
					dbkFMFileArrivalList.setLoaded_hour(rs.getInt("loaded_hour"));
					dbkFMFileArrivalList.setLoaded_time(rs.getInt("loaded_time"));
					dbkFMFileArrivalList.setRecord_count(rs.getInt("size_or_record_count"));
					dbkFMFileArrivalList.setRecord_count_check(rs.getString("size_or_record_count_check"));
					dbkFMFileArrivalList.setColumn_metadata_check(rs.getString("column_metadata_check"));
					dbkFMFileArrivalList.setFile_validity_status(rs.getString("file_validity_status"));
					dbkFMFileArrivalList.setFile_arrival_status(rs.getString("file_arrival_status"));
					dbkFMFileArrivalList.setExpected_hour(rs.getInt("expected_hour"));
					dbkFMFileArrivalList.setExpected_time(rs.getInt("expected_time"));

					return dbkFMFileArrivalList;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			if (dbkFMFileArrivalList == null)
				dbkFMFileArrivalList = new ArrayList<>();
		}
		return dbkFMFileArrivalList;
	}

	@Override
	public List<DbkFMFileArrivalDetails> getDBKFMFileArrivalDetailsForTable(Long idApp, Long connectionId, String from_date, String to_date, String tableOrFileName) {

		List<DbkFMFileArrivalDetails> dbkFMFileArrivalList = null;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL

			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))

				sql = "select * from dbk_fm_filearrival_details where   validation_id  = " + idApp
						+ " and connection_id=" + connectionId + " and load_date BETWEEN '" + from_date
						+ "'::Date AND '" + to_date + "'::Date and table_or_subfolder_name='" + tableOrFileName + "'";
			else
				sql = "select * from dbk_fm_filearrival_details where   validation_id  = " + idApp
						+ " and connection_id=" + connectionId + " and load_date BETWEEN '" + from_date + "' AND '"
						+ to_date + "' and table_or_subfolder_name='" + tableOrFileName + "'";
			
			LOG.debug("sql "+sql);
			dbkFMFileArrivalList = jdbcTemplate1.query(sql, new RowMapper<DbkFMFileArrivalDetails>() {
				@Override
				public DbkFMFileArrivalDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					DbkFMFileArrivalDetails dbkFMFileArrivalList = new DbkFMFileArrivalDetails();

					long connectionId = rs.getLong("connection_id");

					String connectionName = "";
					String connectionType = "";
					try {
						// Get Connection Name
						String con_sql = "Select schemaName,schemaType from listDataSchema where idDataSchema=?";
						Map<String,Object> conDetailsMap = jdbcTemplate.queryForMap(con_sql, connectionId);
						connectionName = (String) conDetailsMap.get("schemaName");
						connectionType = (String) conDetailsMap.get("schemaType");
					} catch (Exception e) {
						LOG.error("exception "+e.getMessage());
						e.printStackTrace();
					}

					dbkFMFileArrivalList.setConnection_id(connectionId);
					dbkFMFileArrivalList.setConnectionName(connectionName);
					dbkFMFileArrivalList.setConnectionType(connectionType);
					dbkFMFileArrivalList.setValidation_id(idApp);
					dbkFMFileArrivalList.setSchema_name(rs.getString("schema_name"));
					dbkFMFileArrivalList.setTable_or_subfolder_name(rs.getString("table_or_subfolder_name"));
					dbkFMFileArrivalList.setFile_indicator(rs.getString("file_indicator"));
					dbkFMFileArrivalList.setDayOfWeek(rs.getString("dayOfWeek"));
					dbkFMFileArrivalList.setLoad_date(rs.getString("load_date"));
					dbkFMFileArrivalList.setLoaded_hour(rs.getInt("loaded_hour"));
					dbkFMFileArrivalList.setLoaded_time(rs.getInt("loaded_time"));
					dbkFMFileArrivalList.setRecord_count(rs.getInt("size_or_record_count"));
					dbkFMFileArrivalList.setRecord_count_check(rs.getString("size_or_record_count_check"));
					dbkFMFileArrivalList.setColumn_metadata_check(rs.getString("column_metadata_check")== null? "" :rs.getString("column_metadata_check"));
					dbkFMFileArrivalList.setFile_validity_status(rs.getString("file_validity_status")== null? "" :rs.getString("file_validity_status"));
					dbkFMFileArrivalList.setFile_arrival_status(rs.getString("file_arrival_status"));
					dbkFMFileArrivalList.setExpected_hour(rs.getInt("expected_hour"));
					dbkFMFileArrivalList.setExpected_time(rs.getInt("expected_time"));

					return dbkFMFileArrivalList;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			if (dbkFMFileArrivalList == null)
				dbkFMFileArrivalList = new ArrayList<>();
		}
		return dbkFMFileArrivalList;
	}
	
	@Override
	public String getIdAppsForFMconnectiondetails(long domainId, long projectId) {
		String idApps = null;
		try {

			String sql = "select fm.idapp from fm_connection_details fm join listDataSchema lds on lds.idDataSchema=fm.idDataSchema where lds.action='Yes'  and lds.domain_id="
					+ domainId + " and lds.project_id=" + projectId + "";

			LOG.debug("sql "+sql);
			List<Long> validationList = jdbcTemplate.queryForList(sql, Long.class);
			List<String> validationList2 = validationList.stream().map(String::valueOf).collect(Collectors.toList());

			// converting list to comma separated String
			idApps = String.join(",", validationList2);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return idApps;
	}

	@Override
	public JSONArray getfailedFileCount(String fromDate, String toDate, String validationList) {
		JSONArray failedfilecount = new JSONArray();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String query = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				query = "select load_date, count(*) as failedfilecount from dbk_fm_filearrival_details where file_validity_status='failed' AND load_date BETWEEN '"
						+ fromDate + "'::Date AND '" + toDate + "'::Date AND validation_id in(" + validationList
						+ ")group by Load_date";
			} else {
				query = "select load_date, count(*) as failedfilecount from dbk_fm_filearrival_details where file_validity_status='failed' AND load_date BETWEEN '"
						+ fromDate + "' AND '" + toDate + "' AND validation_id in(" + validationList
						+ ")group by Load_date";
			}
			
			LOG.debug("query "+query);

			SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(query);
			while (resultSet.next()) {
				try {
					JSONObject obj = new JSONObject();
					String date = "" + resultSet.getObject("load_date");
					long failedFileCount = Long.parseLong("" + resultSet.getObject("failedfilecount"));
					obj.put("date", date);
					obj.put("count", failedFileCount);
					failedfilecount.put(obj);
				} catch (Exception e) {
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return failedfilecount;
	}

	@Override
	public JSONArray getAdditionalFileCount(String fromDate, String toDate, String validationList) {
		JSONArray additionalFileCount = new JSONArray();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String query = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				query = "select load_date, count(*) as additionalfilecount from dbk_fm_filearrival_details where file_arrival_status='additional' AND load_date BETWEEN '"
						+ fromDate + "'::Date AND '" + toDate + "'::Date AND validation_id in(" + validationList
						+ ")group by Load_date";
			} else {
				query = "select load_date, count(*) as additionalfilecount from dbk_fm_filearrival_details where file_arrival_status='additional' AND load_date BETWEEN '"
						+ fromDate + "' AND '" + toDate + "' AND validation_id in(" + validationList
						+ ")group by Load_date";
			}
			
			LOG.debug("query "+query);

			SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(query);
			while (resultSet.next()) {
				try {
					JSONObject obj = new JSONObject();
					String date = "" + resultSet.getObject("load_date");
					long addFileCount = Long.parseLong("" + resultSet.getObject("additionalFileCount"));
					obj.put("date", date);
					obj.put("count", addFileCount);
					additionalFileCount.put(obj);
				} catch (Exception e) {
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return additionalFileCount;
	}

	@Override
	public JSONArray getMissingDelayedFileCount(String fromDate, String toDate, String validationList) {
		JSONArray missingDelayedFileCount = new JSONArray();
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String query = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

				query = "select load_date, count(*) as missedDelayedCount from dbk_fm_summary_details where status='missing' AND load_date BETWEEN '"
						+ fromDate + "'::Date AND '" + toDate + "'::Date AND validation_id in(" + validationList
						+ ")group by Load_date";
			} else {
				query = "select load_date, count(*) as missedDelayedCount from dbk_fm_summary_details where status='missing' AND load_date BETWEEN '"
						+ fromDate + "' AND '" + toDate + "' AND validation_id in(" + validationList
						+ ")group by Load_date";
			}
			LOG.debug("query "+query);

			SqlRowSet resultSet = jdbcTemplate1.queryForRowSet(query);
			while (resultSet.next()) {
				try {
					JSONObject obj = new JSONObject();
					String date = "" + resultSet.getObject("load_date");
					long missingFileCount = Long.parseLong("" + resultSet.getObject("missedDelayedCount"));
					obj.put("date", date);
					obj.put("count", missingFileCount);
					missingDelayedFileCount.put(obj);
				} catch (Exception e) {
					LOG.error("exception "+e.getMessage());
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return missingDelayedFileCount;
	}

}


