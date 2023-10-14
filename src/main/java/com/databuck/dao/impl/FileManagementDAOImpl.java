package com.databuck.dao.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.FingerprintMatchingDashboard;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.IFileManagementDAO;

@Repository
public class FileManagementDAOImpl implements IFileManagementDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	
	private static final Logger LOG = Logger.getLogger(FileManagementDAOImpl.class);

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate1) {
		this.jdbcTemplate1 = jdbcTemplate1;
		this.jdbcTemplate = jdbcTemplate;
	}

	public SqlRowSet getModelGovernanceDashboardFromListApplications() {
		String sql = "select idApp,name from listApplications where appType='Model Governance Dashboard'";
		SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
		return queryForRowSet;
	}

	public SqlRowSet getcsvDirectoryFromListApplications(Long idApp) {
		try {
			String sql = "select csvDir,name from listApplications where idApp=" + idApp;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getAllModelsFromModelGovernanceResultsTable(String decileEqualityTableName, String date) {
		List<String> all = new ArrayList<String>();
		String query = "SELECT DISTINCT MODEL_ID FROM " + decileEqualityTableName + " WHERE DATE =  '" + date + "'";
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(query);
		while (queryForRowSet.next()) {
			all.add(queryForRowSet.getString("MODEL_ID"));
		}
		return all;
	}

	public List<String> getfailedModelsFromModelGovernanceResultsTable(String decileEqualityTableName, String date,
			String statusColumnName) {
		List<String> all = new ArrayList<String>();
		String query = "SELECT DISTINCT MODEL_ID FROM " + decileEqualityTableName + " WHERE DATE =  '" + date + "' AND "
				+ statusColumnName + " =  'failed'";
		SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(query);
		while (queryForRowSet.next()) {
			all.add(queryForRowSet.getString("MODEL_ID"));
		}
		return all;
	}

	public SqlRowSet getdatafromresultmaster() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='FM' ORDER BY appID DESC";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	public SqlRowSet getDataFromResultMasterForModelGovernance() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='MG' ORDER BY appID DESC";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	public SqlRowSet getDataFromResultMasterForStatisticalMatching() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='SM' ORDER BY appID DESC ";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	public SqlRowSet getTableNamesForAppIDinResultMaster(Long appId) {
		String sql = "SELECT AppName,appID, Table_Name FROM result_master_table WHERE appID =" + appId;
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public SqlRowSet getTableFromResultsDB(String tableName, Long appId) {
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet("select * from " + tableName);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public LinkedHashMap<String, String> getConfigurationCheckFromlistStatisticalMatchingConfig(Long appId) {
		LinkedHashMap<String, String> hm = new LinkedHashMap<String, String>();
		try {
			String sql = "select measurementSum,measurementMean,measurementStdDev,groupBy from listStatisticalMatchingConfig where idApp="
					+ appId;
			SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
			while (queryForRowSet.next()) {
				hm.put("measurementSum", queryForRowSet.getString(1));
				hm.put("measurementMean", queryForRowSet.getString(2));
				hm.put("measurementStdDev", queryForRowSet.getString(3));
				hm.put("groupBy", queryForRowSet.getString(4));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return hm;
	}

	public SqlRowSet getMaxRecordForDashBoard(String table_Name, Long appId) {
		LinkedHashMap<String, String> listStatisticalMatchingObject = getConfigurationCheckFromlistStatisticalMatchingConfig(
				appId);
		LOG.debug("listStatisticalMatchingObject=" + listStatisticalMatchingObject);
		try {
			String sql = "SELECT Date,Run,rcThreshold as RCThreshold,rcMatchStatus as RCStatus";
			if (listStatisticalMatchingObject.get("measurementSum").equalsIgnoreCase("Y")) {
				sql += ",M_SumThreshold as SumThreshold,M_SumMatchStatus as SumStatus";
			}
			if (listStatisticalMatchingObject.get("measurementMean").equalsIgnoreCase("Y")) {
				sql += ",M_MeanThreshold as MeanThreshold,M_MeanMatchStatus as MeanStatus";
			}
			if (listStatisticalMatchingObject.get("measurementStdDev").equalsIgnoreCase("Y")) {
				sql += ",M_StddevThreshold as StddevThreshold,M_StdDevMatchStatus as StdDevStatus";
			}
			sql += " FROM " + table_Name + " WHERE id=(select max(id) from " + table_Name + ")";
			// System.out.println(sql);
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);

			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<FingerprintMatchingDashboard> getDashBoardDataForFingerprintMatching(
			Map<Long, String> resultmasterdata) {

		List<FingerprintMatchingDashboard> masterDashboard = new ArrayList<FingerprintMatchingDashboard>();
		try {
			for (Entry e : resultmasterdata.entrySet()) {
				FingerprintMatchingDashboard dashboard = new FingerprintMatchingDashboard();
				SqlRowSet appData = getAppData((Long) e.getKey());
				while (appData.next()) {
					dashboard.setIdApp((Long) e.getKey());
					dashboard.setValidationCheckName(appData.getString(1));
					dashboard.setSource1(appData.getString(4));
					dashboard.setSource2(getSource2NameFromAppDB(appData.getLong(3)));
					LinkedHashMap<String, String> listStatisticalMatchingObject = getConfigurationCheckFromlistStatisticalMatchingConfig(
							(Long) e.getKey());
					String tableName = "";
					if (listStatisticalMatchingObject.get("groupBy").equalsIgnoreCase("Y")) {
						tableName = "STATISTICAL_MATCHING_" + e.getKey() + "_DGROUP";
					} else {
						tableName = "STATISTICAL_MATCHING_" + e.getKey() + "_SUMMARY";
					}
					SqlRowSet maxRecordForDashBoard = getMaxRecordForDashBoard(tableName, (Long) e.getKey());
					while (maxRecordForDashBoard.next()) {
						dashboard.setDate(maxRecordForDashBoard.getString(1));
						dashboard.setRun(maxRecordForDashBoard.getLong(2));
						dashboard.setRcStatus(maxRecordForDashBoard.getString(4));
						if (listStatisticalMatchingObject.get("measurementSum").equalsIgnoreCase("Y")) {
							dashboard.setSumStatus(maxRecordForDashBoard.getString("SumStatus"));
						} else {
							dashboard.setSumStatus("NA");
						}
						if (listStatisticalMatchingObject.get("measurementMean").equalsIgnoreCase("Y")) {
							dashboard.setMeanStatus(maxRecordForDashBoard.getString("MeanStatus"));
						} else {
							dashboard.setMeanStatus("NA");
						}
						if (listStatisticalMatchingObject.get("measurementStdDev").equalsIgnoreCase("Y")) {
							dashboard.setStdDevStatus(maxRecordForDashBoard.getString("StdDevStatus"));
						} else {
							dashboard.setStdDevStatus("NA");
						}
					}
				}
				masterDashboard.add(dashboard);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		createMatchingCsv(masterDashboard);
		return masterDashboard;
	}

	private void createMatchingCsv(List<FingerprintMatchingDashboard> matchingDashboardList) {
		try {
			Collections.reverse(matchingDashboardList);
			PrintWriter pw = new PrintWriter(
					new FileWriter(new File(System.getenv("DATABUCK_HOME") + "/csvFiles/sm_dashboard.csv")));
			String csvHeader = "idApp,date,run,validationCheckName,source1,source2,RCStatus,SumStatus,MeanStatus,StdDevStatus";
			pw.println(csvHeader);
			for (FingerprintMatchingDashboard fmd : matchingDashboardList) {
				StringJoiner csvData = new StringJoiner(",");
				csvData.add(fmd.getIdApp().toString());
				csvData.add(fmd.getDate());
				csvData.add(fmd.getRun().toString());
				csvData.add(fmd.getValidationCheckName());
				csvData.add(fmd.getSource1());
				csvData.add(fmd.getSource2());
				csvData.add(fmd.getRcStatus());
				csvData.add(fmd.getSumStatus());
				csvData.add(fmd.getMeanStatus());
				csvData.add(fmd.getStdDevStatus());
				pw.println(csvData);
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public SqlRowSet getAppData(Long idApp) {
		try {
			String sql = "SELECT la.name,la.idData,la.idRightData,lds.name as idDataName FROM listApplications la, listDataSources lds "
					+ "WHERE la.idData=lds.idData and idApp=" + idApp;
			return jdbcTemplate.queryForRowSet(sql);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public String getSource2NameFromAppDB(Long idRightData) {
		try {
			return jdbcTemplate.queryForObject("select name from listDataSources where idData=" + idRightData,
					String.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public SqlRowSet getTableFromResultsDBForStatisticalMatching(String tableName, Long appId, String appType) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				if (appType.equalsIgnoreCase("summary")) {
					sql = "select id,Date,Run,round(rcDifference::numeric,2) as rcDifference,round(rcThreshold::numeric,2) as rcThreshold,rcThresholdType,rcMatchStatus,"
							+ "M_SumDifference,round(M_SumThreshold::numeric,2) as M_SumThreshold,M_SumThresholdType,M_SumMatchStatus,round(M_MeanDifference::numeric,2) as "
							+ "M_MeanDifference,round(M_MeanThreshold::numeric,2) as M_MeanThreshold,M_MeanThresholdType,M_MeanMatchStatus,"
							+ "round(M_StddevDifference::numeric,2) as M_StddevDifference,round(M_StddevThreshold::numeric,2) as M_StddevThreshold,M_StddevThresholdType,"
							+ "M_StdDevMatchStatus from " + tableName;
				} else {
					sql = "select id,Date,Run,dGroupVal,round(rcDifference::numeric,2) as rcDifference,round(rcThreshold::numeric,2) as rcThreshold,rcThresholdType,rcMatchStatus,"
							+ "M_SumDifference,round(M_SumThreshold::numeric,2) as M_SumThreshold,M_SumThresholdType,M_SumMatchStatus,round(M_MeanDifference::numeric,2) as "
							+ "M_MeanDifference,round(M_MeanThreshold::numeric,2) as M_MeanThreshold,M_MeanThresholdType,M_MeanMatchStatus,"
							+ "round(M_StddevDifference::numeric,2) as M_StddevDifference,round(M_StddevThreshold::numeric,2) as M_StddevThreshold,M_StddevThresholdType,"
							+ "M_StdDevMatchStatus from " + tableName;
				}
			} else {
				if (appType.equalsIgnoreCase("summary")) {
					sql = "select id,Date,Run,format(rcDifference,2) as rcDifference,format(rcThreshold,2) as rcThreshold,rcThresholdType,rcMatchStatus,"
							+ "M_SumDifference,format(M_SumThreshold,2) as M_SumThreshold,M_SumThresholdType,M_SumMatchStatus,format(M_MeanDifference,2) as "
							+ "M_MeanDifference,format(M_MeanThreshold,2) as M_MeanThreshold,M_MeanThresholdType,M_MeanMatchStatus,"
							+ "format(M_StddevDifference,2) as M_StddevDifference,format(M_StddevThreshold,2) as M_StddevThreshold,M_StddevThresholdType,"
							+ "M_StdDevMatchStatus from " + tableName;
				} else {
					sql = "select id,Date,Run,dGroupVal,format(rcDifference,2) as rcDifference,format(rcThreshold,2) as rcThreshold,rcThresholdType,rcMatchStatus,"
							+ "M_SumDifference,format(M_SumThreshold,2) as M_SumThreshold,M_SumThresholdType,M_SumMatchStatus,format(M_MeanDifference,2) as "
							+ "M_MeanDifference,format(M_MeanThreshold,2) as M_MeanThreshold,M_MeanThresholdType,M_MeanMatchStatus,"
							+ "format(M_StddevDifference,2) as M_StddevDifference,format(M_StddevThreshold,2) as M_StddevThreshold,M_StddevThresholdType,"
							+ "M_StdDevMatchStatus from " + tableName;
				}
			}
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getTableFromResultsDBForModelGovernance(String table_Name, Long appId,
			String modelGovernanceType) {
		try {
			String sql = "select * from " + table_Name;
			/*
			 * if(modelGovernanceType.equalsIgnoreCase("DecileConsistency")){
			 * sql=
			 * "SELECT DATE, RUN, ROUND( MODEL_ID, 2 ) AS MODEL_ID, ROUND( MODEL_SCORE_DECILE, 2 ) AS MODEL_SCORE_DECILE, "
			 * +
			 * "ROUND( COUNT, 2 ) AS COUNT, ROUND( countPercentage, 2 ) AS countPercentage, decileConsistencyStatus FROM "
			 * +table_Name; }else
			 * if(modelGovernanceType.equalsIgnoreCase("DecileEquality")){ sql=
			 * "SELECT DATE, RUN, ROUND( MODEL_ID, 2 ) AS MODEL_ID, ROUND( MODEL_SCORE_DECILE, 2 ) AS MODEL_SCORE_DECILE,"
			 * +
			 * " ROUND( COUNT, 2 ) AS COUNT, ROUND( countPercentage, 2 ) AS countPercentage, Threshold, decileEqualityStatus FROM  "
			 * +table_Name; }else
			 * if(modelGovernanceType.equalsIgnoreCase("ScoreConsistency")){
			 * sql=
			 * "SELECT Date, RUN, ROUND( L_MODEL_ID, 2 ) AS L_MODEL_ID, ROUND( L_MODEL_SCORE_DECILE, 2 ) AS L_MODEL_SCORE_DECILE,"
			 * +
			 * "RecordCount, ROUND( AvgChange, 2 ) AS AvgChange, ROUND( avgAbsChange, 2 ) AS avgAbsChange,ROUND( stddevOFAbsChange, 2 ) AS stddevOFAbsChange FROM  "
			 * +table_Name; }else
			 * if(modelGovernanceType.equalsIgnoreCase("ScoreStatus")){ sql=
			 * "SELECT Date, RUN, ROUND( L_MODEL_ID, 2 ) AS L_MODEL_ID, ROUND( MODEL_ID, 2 ) AS MODEL_ID,"
			 * + "RecordCount,totalRecordCount,status FROM  "+table_Name; }
			 */

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getDataFromResultMasterForDataMatchingGroup() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='DMG'";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	public String getApplicationNameFromApplicationName(String string) {
		try {
			String sql = "select name from listApplications where idApp=" + string;
			return jdbcTemplate.queryForObject(sql, String.class);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return string;

	}

	public SqlRowSet getDataFromResultMasterForSchemaMatching() {
		SqlRowSet queryForRowSet = null;
		String sql = "SELECT distinct appID,AppName FROM result_master_table where AppType='SCM' ORDER BY appID DESC ";
		try {
			queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
		} catch (org.springframework.dao.RecoverableDataAccessException e) {
			LOG.error(e.getMessage());
			try {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			} catch (Exception e1) {
				LOG.error("problem with connection pool");
				queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			}
		}
		return queryForRowSet;
	}

	public SqlRowSet getTableDataForSchemMatching(String tableName) {
		try {

			String sql = "SELECT * FROM " + tableName;
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public SqlRowSet getTableNamesForAppIDinResultMasterForSchemaMatching(Long appId, String resultType) {
		String sql = "SELECT AppName,appID, Table_Name FROM result_master_table WHERE appID =" + appId
				+ " AND Result_Type='" + resultType + "' ";
		LOG.debug("sql:" + sql);
		try {
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			return queryForRowSet;
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
}
