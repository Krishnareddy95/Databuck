package com.databuck.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.databuck.bean.DataMatchingSummary;
import com.databuck.bean.KeyMeasurementMatchingDashboard;
import com.databuck.bean.PrimaryMatchingSummary;
import com.databuck.dao.MatchingResultDao;
import com.databuck.service.IMatchingResultService;

@Service
public class MatchingResultService implements IMatchingResultService {

	@Autowired
	public MatchingResultDao matchingResultDao;

	public Map<Long, String> getMatchingResultTable() {
		return matchingResultDao.getMatchingResultTable();
	}
	
	public Map<Long, String> getRollDataMatchingResultTable() {
		return matchingResultDao.getRollDataMatchingResultTable();
	}

	public SqlRowSet getDataMatchingResultsTableNames(Long appId) {
		return matchingResultDao.getDataMatchingResultsTableNames(appId);
	}
	
	public SqlRowSet getRollDataMatchingResultsTableNames(Long appId) {
		return matchingResultDao.getRollDataMatchingResultsTableNames(appId);
	}

	public Object[] getDataFromResultTable(String tableName, Long appId) {
		return matchingResultDao.getDataFromResultTable(tableName, appId);
	}

	public Map<String, String> getDataFromDataMatchingSummary(String tableName) {
		return matchingResultDao.getDataFromDataMatchingSummary(tableName);
	}

	public SqlRowSet getThresholdFromListApplication(Long idApp) {
		return matchingResultDao.getThresholdFromListApplication(idApp);
	}

	public List<DataMatchingSummary> getDataFromDataMatchingSummaryGroupByDate(String tableName) {
		return matchingResultDao.getDataFromDataMatchingSummaryGroupByDate(tableName);
	}

	public SqlRowSet getUnmatchedGroupbyTableData(String tableName) {
		return matchingResultDao.getUnmatchedGroupbyTableData(tableName);
	}

	public String getAppNameFromListApplication(Long idApp) {
		return matchingResultDao.getAppNameFromListApplication(idApp);
	}

	@Override
	public SqlRowSet getDataForPrimary(String tableName, Long appId) {
		return matchingResultDao.getDataForPrimary(tableName, appId);
	}
	
	public Object[] getZeroStatusAbdCount(String tableName, Long appId) {
		return matchingResultDao.getZeroStatusAbdCount(tableName, appId);
		}

	@Override
	public SqlRowSet getRollDataSummary(String tableName, long SchemaId) {
		return matchingResultDao.getRollDataSummary(tableName, SchemaId);
	}

	@Override
	public long getRollDataSummaryCount(String tableName, long rollTargetSchemaId) {
		return matchingResultDao.getRollDataSummaryCount(tableName, rollTargetSchemaId);
	}

	@Override
	public SqlRowSet downloadRollDataSummary(String tableName, long rollTargetSchemaId) {
		return matchingResultDao.downloadRollDataSummary(tableName, rollTargetSchemaId);
	}
	
	// for primary key matching
		@Override
		public Map<Long, String> getPrimaryKeyMatchingResultTable() {
			return matchingResultDao.getPrimaryKeyMatchingResultTable();
		}
		
		@Override
		public SqlRowSet getPrimaryKeyMatchingResultsTableNames(Long appId) {
			return matchingResultDao.getPrimaryKeyMatchingResultsTableNames(appId);
		}

		@Override
		public List<PrimaryMatchingSummary> getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(String tableName) {
			
			return matchingResultDao.getDataFromPrimaryKeyDataMatchingSummaryGroupByDate(tableName);
		}

		@Override
		public Object[] getZeroStatusAbdCountForPrimaryKeyMatching(String tableName, Long appId) {
			
			return matchingResultDao.getZeroStatusAbdCountForPrimaryKeyMatching(tableName, appId);
		}

		@Override
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummary(String tableName) {
			
			return matchingResultDao.getDataFromPrimaryKeyDataMatchingSummary(tableName);
		}

		@Override
		public List<PrimaryMatchingSummary> getLeftGraphForPrimaryKeyMatching(String tableName) {

			return matchingResultDao.getLeftGraphForPrimaryKeyMatching(tableName);
		}

		@Override
		public List<PrimaryMatchingSummary> getUnmatchedGraphForPrimaryKeyMatching(String tableName) {

			return matchingResultDao.getUnmatchedGraphForPrimaryKeyMatching(tableName);
		}

		@Override
		public List<KeyMeasurementMatchingDashboard> getPrimaryKeyMatchingDashboard() {
		
			return matchingResultDao.getPrimaryKeyMatchingDashboard();
		}

		@Override
		public String getPrimaryKeyMatchingResultTableColumns(Long nAppId) {

			return matchingResultDao.getPrimaryKeyMatchingResultTableColumns(nAppId);
		}

		@Override
		public Map<String, String> getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate(String tableName) {
			return matchingResultDao.getDataFromPrimaryKeyDataMatchingSummaryByMaxRunNRecentDate(tableName);
		}
		
		
		
		
	
}