package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Connection;

import com.databuck.bean.DBKFileMonitoringRules;
import com.databuck.bean.DbkFMFileArrivalDetails;
import com.databuck.bean.DbkFMHistory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import com.databuck.bean.FileMonitorMasterDashboard;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;
import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataSource;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DataSourceType;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.FileMonitorDao;

import org.apache.log4j.Logger;

@Repository
public class FileMonitorDaoImpl implements FileMonitorDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    private Properties resultDBConnectionProperties;
    
    private static final Logger LOG = Logger.getLogger(FileMonitorDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileMonitorRules> getCurrentTimeMonitorRules(String timeStr, int currentDay) {
	String curMinutes = timeStr.split(":")[1];
	String query = "";
	if (curMinutes.equals("00")) {
	    ;
	    query = "From FileMonitorRules where frequency='hourly' or (frequency='weekly' and dayOfCheck=" + currentDay
		    + " and timeOfCheck='" + timeStr + "') or (frequency='daily' and timeOfCheck='" + timeStr + "') ";
	} else {
	    query = "From FileMonitorRules where  ((frequency='weekly' and dayOfCheck=" + currentDay
		    + ") or  frequency='daily') and timeOfCheck='" + timeStr + "'";
	}
	List<FileMonitorRules> rulesList = (List<FileMonitorRules>) sessionFactory.getCurrentSession()
		.createQuery(query).list();
	return rulesList;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileTrackingSummary> getActiveRulesSummary(String dailyRulesIds, String trackingDate, String timeStr,
	    int currentDay) {
	String query = "from FileTrackingSummary fs where  fs.fileMonitorRules.id in (" + dailyRulesIds + ") and "
		+ "fs.date='" + trackingDate + "' and fs.hourOfDay='" + timeStr + "' and"
		+ "(fs.fileMonitorRules.frequency='daily' or fs.fileMonitorRules.frequency='hourly' or (fs.fileMonitorRules.frequency='weekly' and fs.dayOfWeek="
		+ currentDay + "))";
	List<FileTrackingSummary> rulesSummaryList = (List<FileTrackingSummary>) sessionFactory.getCurrentSession()
		.createQuery(query).list();
	return rulesSummaryList;
    }

    @Transactional
    public void addFileTrackingHistoryRequest(FileTrackingHistory fileTrackingHistory) {
	sessionFactory.getCurrentSession().saveOrUpdate(fileTrackingHistory);
    }

    @Transactional(readOnly = true)
    public FileTrackingHistory getFileTrackingHistoryRequest(long id) {
	FileTrackingHistory request = (FileTrackingHistory) sessionFactory.getCurrentSession()
		.get(FileTrackingHistory.class, id);
	return request;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileMonitorRules> findFileMonitorRules(String bucketName, String folderPath) {
	String query = "from FileMonitorRules where  bucketName='" + bucketName + "' and folderPath='" + folderPath
		+ "'";
	List<FileMonitorRules> rulesList = (List<FileMonitorRules>) sessionFactory.getCurrentSession()
		.createQuery(query).list();
	return rulesList;
    }

    @Transactional
    public void addFileTrackingSummary(FileTrackingSummary fileTrackingSummary) {
	sessionFactory.getCurrentSession().saveOrUpdate(fileTrackingSummary);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileTrackingSummary> getFileTrackingSummaryForRule(long fMonitorRuleId, String trackingDate,
	    Integer dayOfWeek, String hourOfDay) {
	String query = "From FileTrackingSummary where fileMonitorRules.id=" + fMonitorRuleId + " and dayOfWeek="
		+ dayOfWeek + " and hourOfDay='" + hourOfDay + "' and date='" + trackingDate + "'";
	List<FileTrackingSummary> rulesList = (List<FileTrackingSummary>) sessionFactory.getCurrentSession()
		.createQuery(query).list();
	return rulesList;
    }

    @Transactional
    public boolean addMonitorRule(FileMonitorRules fileMonitorRules) {

	LOG.debug("In addMonitorRule =>" + fileMonitorRules.toString());

	sessionFactory.getCurrentSession().saveOrUpdate(fileMonitorRules);
	LOG.info("FM Added successfully!!!!!!!!!");
	return true;

    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileTrackingHistory> getFileTrackingHistoryDetails(Long idApp) {

	String query = "from FileTrackingHistory where idApp = " + idApp;

	LOG.debug("FileTrackingHistory query =>" + query);

	List<FileTrackingHistory> rulesList = (List<FileTrackingHistory>) sessionFactory.getCurrentSession()
		.createQuery(query).list();

	/* LOG.debug("historyList =>" + rulesList.toString()); */

	return rulesList;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileTrackingSummary> getFileTrackingRulesSummaryDetails(Long idApp) {

	String query = "from FileTrackingSummary where idApp=" + idApp;
	LOG.debug("FileTrackingSummary =>" + query);
	List<FileTrackingSummary> summList = (List<FileTrackingSummary>) sessionFactory.getCurrentSession()
		.createQuery(query).list();

	return summList;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileMonitorRules> getAllFileMonitorRuleDetailsByIdApp(long idApp) {

	String query = "from FileMonitorRules where idApp=" + idApp;
	LOG.debug("FileMonitorRule =>" + query);

	List<FileMonitorRules> fileMonitorRuleList = (List<FileMonitorRules>) sessionFactory.getCurrentSession()
		.createQuery(query).list();

	LOG.debug("fileMonitorRuleList =>" + fileMonitorRuleList.toString());

	return fileMonitorRuleList;
    }

    @Transactional(readOnly = true)
    public FileMonitorRules getFileMonitorRulesById(long id) {

	FileMonitorRules fileMonitorRules = null;
	try {
	    fileMonitorRules = (FileMonitorRules) this.sessionFactory.getCurrentSession().get(FileMonitorRules.class,
		    id);
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return fileMonitorRules;

    }

    @Transactional
    public int updateFileMonitorRulesById(long id, FileMonitorRules fm) {

	LOG.debug("In updateFileMonitorRulesById..........." + fm.toString());
	Session session = this.sessionFactory.getCurrentSession();
	session.update(fm);

	LOG.info("FM Updated successfully!!!!!!!!!");
	return 1;

    }

    @Transactional
    public boolean deleteFileMonitorRuleById(long id) {

	String query = "From FileMonitorRules where id=" + id;
	LOG.debug("In deleteFileMonitorRuleById------------" + query);

	try {
	    Session session = this.sessionFactory.getCurrentSession();

	    FileMonitorRules fm = (FileMonitorRules) sessionFactory.getCurrentSession().get(FileMonitorRules.class, id);

	    LOG.debug("File Monitor Rules" + fm.toString());

	    if (fm != null) {
		session.delete(fm);

		LOG.info("FM Deleted................");

	    }

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}

	return true;
    }

    @Override
    public List<FileMonitorMasterDashboard> getFileMonitoringAppsList() {

	List<FileMonitorMasterDashboard> appList = new ArrayList<FileMonitorMasterDashboard>();

	try {
	    String sql = "select t.idApp, (select la.name from listApplications la where la.idApp=t.idApp) as name, t.Date, (select max(run) as Run from file_tracking_summary where idApp=t.idApp and t.Date=Date) as Run from (select max(Date) as Date, idApp from file_tracking_summary group by idApp) t;";

	    appList = jdbcTemplate.query(sql, new RowMapper<FileMonitorMasterDashboard>() {
		@Override
		public FileMonitorMasterDashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
		    FileMonitorMasterDashboard fmMasterDashboard = new FileMonitorMasterDashboard();
		    fmMasterDashboard.setIdApp(rs.getLong("idApp"));
		    fmMasterDashboard.setValidationCheckName(rs.getString("name"));
		    fmMasterDashboard.setDate(rs.getString("Date"));
		    fmMasterDashboard.setRun(rs.getLong("Run"));
		    return fmMasterDashboard;
		}
	    });
	} catch (Exception e) {
	    LOG.error("\n Exception occurred while fetching the list of Applications for fileMonitoring !!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return appList;
    }

    @Override
    public List<FileMonitorMasterDashboard> getDbkFileMonitoringAppsList() {

	List<FileMonitorMasterDashboard> appList = new ArrayList<FileMonitorMasterDashboard>();

	try {

	    // Query compatibility changes for both POSTGRES and MYSQL
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

		String sql = "select validation_id as idApp, max(load_date) as load_date, '1' AS Run from dbk_fm_summary_details group by validation_id";

		LOG.debug("SQLQuery:" + sql);
		appList = jdbcTemplate1.query(sql, new RowMapper<FileMonitorMasterDashboard>() {
		    @Override
		    public FileMonitorMasterDashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
			FileMonitorMasterDashboard fmMasterDashboard = new FileMonitorMasterDashboard();
			long idApp = rs.getLong("idApp");
			String validationName = "";
			// Get validation Id name from database
			try {
			    String nameSql = "Select name from listApplications where idApp=" + idApp;
			    validationName = jdbcTemplate.queryForObject(nameSql, String.class);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			    e.printStackTrace();
			}
			fmMasterDashboard.setIdApp(idApp);
			fmMasterDashboard.setValidationCheckName(validationName);
			fmMasterDashboard.setDate(rs.getString("load_date"));
			fmMasterDashboard.setRun(rs.getLong("Run"));
			return fmMasterDashboard;
		    }
		});
	    } else {
		String resultDBSchemaName = resultDBConnectionProperties.getProperty("db1.schema.name");

		String sql = "select t.idApp, la.name, t.load_date as Date, t.Run from (select validation_id as idApp, max(load_date) as load_date, '1' AS Run from "
			+ resultDBSchemaName
			+ ".dbk_fm_summary_details group by validation_id) t join listApplications la on t.idApp=la.idApp";

		appList = jdbcTemplate.query(sql, new RowMapper<FileMonitorMasterDashboard>() {
		    @Override
		    public FileMonitorMasterDashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
			FileMonitorMasterDashboard fmMasterDashboard = new FileMonitorMasterDashboard();
			fmMasterDashboard.setIdApp(rs.getLong("idApp"));
			fmMasterDashboard.setValidationCheckName(rs.getString("name"));
			fmMasterDashboard.setDate(rs.getString("Date"));
			fmMasterDashboard.setRun(rs.getLong("Run"));
			return fmMasterDashboard;
		    }
		});
	    }

	} catch (Exception e) {
	    LOG.error("\n Exception occurred while fetching the list of Applications for fileMonitoring !!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return appList;
    }

    @Override
    public List<FileMonitorRules> getFileMonitorRulesByAppId(long idApp) {
	String query = "select * from file_monitor_rules where idApp=?";

	RowMapper<FileMonitorRules> rowMapper = (rs, rowNum) -> {

	    FileMonitorRules fmRules = new FileMonitorRules();
	    fmRules.setId(rs.getLong("id"));
	    fmRules.setIdApp(rs.getInt("idApp"));
	    fmRules.setBucketName(rs.getString("bucketName"));
	    fmRules.setDayOfCheck(rs.getInt("dayOfCheck"));
	    fmRules.setFileCount(rs.getInt("fileCount"));
	    fmRules.setFilePattern(rs.getString("filePattern"));
	    fmRules.setFolderPath(rs.getString("folderPath"));
	    fmRules.setFrequency(rs.getString("frequency"));
	    fmRules.setTimeOfCheck(rs.getString("timeOfCheck"));
	    fmRules.setFileSizeThreshold(rs.getInt("fileSizeThreshold"));
	    fmRules.setLastProcessedDate(rs.getTimestamp("lastProcessedDate"));
	    fmRules.setPartitionedFolders(rs.getString("partitionedFolders"));
	    fmRules.setMaxFolderDepth(rs.getInt("maxFolderDepth"));
	    fmRules.setIdDataSchema(rs.getLong("idDataSchema"));
	    return fmRules;
	};
	List<FileMonitorRules> fmRulesList = jdbcTemplate.query(query, rowMapper, idApp);
	return fmRulesList;
    }

    @Override
    public void updateFMRuleLastProcessingTime(long idApp, long fmRuleId, Date lastProcessedTime) {
	String query = "update file_monitor_rules set lastProcessedDate=? where id=? and idApp=?";
	try {
	    jdbcTemplate.update(query, lastProcessedTime, fmRuleId, idApp);
	} catch (Exception e) {
	    LOG.error("\n=====> Exception occurred in updateFMRuleLastProcessingTime !!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public Date getMaxDateForRule(long idApp, long fmruleId) {
	String query = "select Max(Date) from file_tracking_summary where idApp=? and fileMonitorRules_id=?";
	Date maxDate = null;
	try {
	    maxDate = jdbcTemplate.queryForObject(query, Date.class, idApp, fmruleId);
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to get Max Run for this date, hence reseting to Zero!!!!");
	    LOG.error(e.getMessage());
	}
	return maxDate;
    }

    @Override
    public long getAvgFileSizeOfRule(long idApp, long fmRuleId) {
	String query = "select (sum(fh.totalFileSize)/count(*)) from (select sum(fileSize) as totalFileSize from file_tracking_history where idApp=? and fileMonitorRuleId=? group by date,run) fh ";
	long avgFileSize = 0l;
	try {
	    avgFileSize = jdbcTemplate.queryForObject(query, Long.class, idApp, fmRuleId);
	} catch (Exception e) {
		LOG.error(e.toString());
	}
	return avgFileSize;
    }

    @Override
    public List<ListApplications> getContinuousFileMonitoringAppsList() {
	List<ListApplications> appList = new ArrayList<ListApplications>();
	try {
	    String sql = "select * from listApplications where active='yes' and appType='File Monitoring' and continuousFileMonitoring='Y'";
	    appList = jdbcTemplate.query(sql, new RowMapper<ListApplications>() {

		@Override
		public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
		    ListApplications la = new ListApplications();
		    la.setIdApp(rs.getLong("idApp"));
		    la.setName(rs.getString("name"));
		    la.setFileMonitoringType(rs.getString("fileMonitoringType"));
		    la.setAppType(rs.getString("appType"));
		    return la;
		}
	    });
	} catch (Exception e) {
	    LOG.error("\n Exception occurred while fetching the list of Applications for fileMonitoring !!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return appList;
    }

    @Override
    public List<ListApplications> getContinuousFileMonitoringAppsListByType(String fileMonitoringType) {
	List<ListApplications> appList = new ArrayList<ListApplications>();
	try {
	    String sql = "select * from listApplications where active='yes' and appType='File Monitoring' and continuousFileMonitoring='Y' and fileMonitoringType=?";
	    appList = jdbcTemplate.query(sql, new RowMapper<ListApplications>() {

		@Override
		public ListApplications mapRow(ResultSet rs, int rowNum) throws SQLException {
		    ListApplications la = new ListApplications();
		    la.setIdApp(rs.getLong("idApp"));
		    la.setName(rs.getString("name"));
		    la.setFileMonitoringType(rs.getString("fileMonitoringType"));
		    la.setAppType(rs.getString("appType"));
		    return la;
		}
	    }, fileMonitoringType);
	} catch (Exception e) {
	    LOG.error("\n Exception occurred while fetching the list of Applications for fileMonitoring !!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return appList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public FileTrackingSummary getLatestFileTrackingSummaryForRule(long idApp, long fmruleId, String trackingDate) {
	FileTrackingSummary fileTrackingSummary = null;
	try {
	    String query = "from FileTrackingSummary fs where fs.idApp=" + idApp + " and fs.fileMonitorRules.id="
		    + fmruleId + " and fs.date='" + trackingDate + "' order by fs.run desc";

	    List<FileTrackingSummary> summryList = (List<FileTrackingSummary>) sessionFactory.getCurrentSession()
		    .createQuery(query).list();
	    if (summryList != null && summryList.size() > 0) {
		fileTrackingSummary = summryList.get(0);
	    }
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to get Latest Summary of Rule!!!!");
	    LOG.error(e.getMessage());
	}
	return fileTrackingSummary;
    }

    @Override
    public ListDataSource getDataSourceForFilePathAndPattern(String folderPath, String filePattern) {
	ListDataSource listDataSource = null;
	try {
	    String sql = "select t1.* from listDataSources t1 join listDataSchema t2 on t1.idDataSchema=t2.idDataSchema and t1.dataLocation='FileSystem Batch' and t1.active='yes' and t2.folderPath=? and t2.fileNamePattern=? order by t1.idData desc limit 1";

	    RowMapper<ListDataSource> rowMapper = new RowMapper<ListDataSource>() {
		@Override
		public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
		    ListDataSource alistdatasource = new ListDataSource();
		    alistdatasource.setIdData(rs.getInt("idData"));
		    alistdatasource.setName(rs.getString("name"));
		    alistdatasource.setDescription(rs.getString("description"));
		    alistdatasource.setDataLocation(rs.getString("dataLocation"));
		    alistdatasource.setDataSource(rs.getString("dataSource"));
		    alistdatasource.setCreatedAt(rs.getDate("createdAt"));
		    alistdatasource.setCreatedBy(rs.getInt("createdBy"));
		    alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
		    return alistdatasource;
		}
	    };

	    List<ListDataSource> listDataSources = jdbcTemplate.query(sql, rowMapper, folderPath, filePattern);
	    if (listDataSources != null && listDataSources.size() > 0) {
		listDataSource = listDataSources.get(0);
	    }
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to get Datasource for folderPath and FilePattern!!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return listDataSource;
    }

    @Override
    public void updateFolderNameOfTemplate(long idData, String folderName) {
	try {
	    String sql = "update listDataAccess set folderName=? where idData=?";
	    jdbcTemplate.update(sql, folderName, idData);
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to update folderName in template!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public Long getDQApplicationsForIdData(Long idData) {
	Long idApp = 0L;
	try {
	    String sql = "select max(idApp) as idApp from listApplications where active='yes' and appType='Data Forensics' and keyGroupRecordCountAnomaly='N' and idData="
		    + idData;
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		idApp = queryForRowSet.getLong("idApp");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return idApp;
    }

    @Override
    public String getOverallCountStatusOfFM(long idApp) {
	String status = "";
	try {
	    String query = "select t6.countStatus as countStatus,count(*) as groupCount from (select t4.* from file_tracking_summary t4 join (select t3.fileMonitorRules_id,t3.date,max(t3.run) as run from (select t1.fileMonitorRules_id,t1.date,t1.run from file_tracking_summary t1 join (select fileMonitorRules_id,max(date) as date  from file_tracking_summary where idApp="
		    + idApp
		    + " group by fileMonitorRules_id) t2 on t1.fileMonitorRules_id=t2.fileMonitorRules_id and t1.date=t2.date where t1.idApp="
		    + idApp
		    + ") t3 group by t3.fileMonitorRules_id,t3.date) t5 on t4.fileMonitorRules_id=t5.fileMonitorRules_id and t4.date=t5.date and t4.run=t5.run) t6 group by t6.countStatus";
	    int passedCount = 0;
	    int failedCount = 0;

	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);

	    while (queryForRowSet.next()) {
		String countStatus = queryForRowSet.getString("countStatus");
		int groupCount = queryForRowSet.getInt("groupCount");

		if (countStatus.equalsIgnoreCase("passed")) {
		    passedCount = groupCount;
		} else if (countStatus.equalsIgnoreCase("failed")) {
		    failedCount = groupCount;
		}
	    }

	    if (failedCount > 0) {
		status = "failed";
	    } else if (failedCount == 0 && passedCount > 0) {
		status = "passed";
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return status;
    }

    @Override
    public String getOverallFileSizeStatusOfFM(long idApp) {
	String status = "";
	try {
	    String query = "select t6.fileSizeStatus as fileSizeStatus,count(*) as groupCount from (select t4.* from file_tracking_summary t4 join (select t3.fileMonitorRules_id,t3.date,max(t3.run) as run from (select t1.fileMonitorRules_id,t1.date,t1.run from file_tracking_summary t1 join (select fileMonitorRules_id,max(date) as date  from file_tracking_summary where idApp="
		    + idApp
		    + " group by fileMonitorRules_id) t2 on t1.fileMonitorRules_id=t2.fileMonitorRules_id and t1.date=t2.date where t1.idApp="
		    + idApp
		    + ") t3 group by t3.fileMonitorRules_id,t3.date) t5 on t4.fileMonitorRules_id=t5.fileMonitorRules_id and t4.date=t5.date and t4.run=t5.run) t6 group by t6.fileSizeStatus";
	    int passedCount = 0;
	    int failedCount = 0;

	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(query);

	    while (queryForRowSet.next()) {
		String fileSizeStatus = queryForRowSet.getString("fileSizeStatus");
		int groupCount = queryForRowSet.getInt("groupCount");

		if (fileSizeStatus.equalsIgnoreCase("passed")) {
		    passedCount = groupCount;
		} else if (fileSizeStatus.equalsIgnoreCase("failed")) {
		    failedCount = groupCount;
		}
	    }

	    if (failedCount > 0) {
		status = "failed";
	    } else if (failedCount == 0 && passedCount > 0) {
		status = "passed";
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return status;
    }

    @Override
    public ListDataSource getLatestDataSourceForIdDataSchema(long idDataSchema) {
	ListDataSource listDataSource = null;
	try {
	    String sql = "select t1.* from listDataSources t1 join listDataAccess t2 on t1.idData=t2.idData where t1.active='yes' and t1.idDataSchema=? order by t1.idData desc limit 1";

	    RowMapper<ListDataSource> rowMapper = new RowMapper<ListDataSource>() {
		@Override
		public ListDataSource mapRow(ResultSet rs, int rowNum) throws SQLException {
		    ListDataSource alistdatasource = new ListDataSource();
		    alistdatasource.setIdData(rs.getInt("idData"));
		    alistdatasource.setName(rs.getString("name"));
		    alistdatasource.setDescription(rs.getString("description"));
		    alistdatasource.setDataLocation(rs.getString("dataLocation"));
		    alistdatasource.setDataSource(rs.getString("dataSource"));
		    alistdatasource.setCreatedAt(rs.getDate("createdAt"));
		    alistdatasource.setCreatedBy(rs.getInt("createdBy"));
		    alistdatasource.setIdDataSchema(rs.getLong("idDataSchema"));
		    return alistdatasource;
		}
	    };

	    List<ListDataSource> listDataSources = jdbcTemplate.query(sql, rowMapper, idDataSchema);
	    if (listDataSources != null && listDataSources.size() > 0) {
		listDataSource = listDataSources.get(0);
	    }
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to get Datasource for idDataSchema [" + idDataSchema + "]!!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return listDataSource;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileMonitorRules> getFileMonitorRulesForSchema(long idDataSchema) {

	String query = "from FileMonitorRules where idDataSchema=" + idDataSchema;
	LOG.debug("FileMonitorRule =>" + query);

	List<FileMonitorRules> fileMonitorRuleList = (List<FileMonitorRules>) sessionFactory.getCurrentSession()
		.createQuery(query).list();

	LOG.debug("fileMonitorRuleList =>" + fileMonitorRuleList.toString());

	return fileMonitorRuleList;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<FileTrackingHistory> getValidUnProcessedFilesFromHistory() {
	List<FileTrackingHistory> fmHistoryList = null;
	try {
	    String query = "from FileTrackingHistory where status = 'passed' and fileExecutionStatus='unprocessed' and idData is not null";

	    fmHistoryList = (List<FileTrackingHistory>) sessionFactory.getCurrentSession().createQuery(query).list();

	} catch (Exception e) {
	    LOG.error("\n=====> Failed to getValidUnProcessedFilesFromHistory!!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return fmHistoryList;
    }

    @Override
    public void updateFileExecutionStatusAndMsg(long fileTrackingHistoryId, String fileExecutionStatus,
	    String fileExecutionStatusMsg) {
	try {
	    String sql = "update file_tracking_history set fileExecutionStatus=? , fileExecutionStatusMsg=? where id=?";
	    jdbcTemplate.update(sql, fileExecutionStatus, fileExecutionStatusMsg, fileTrackingHistoryId);
	} catch (Exception e) {
	    LOG.error("\n=====> Failed to updateFileExecutionStatusAndMsg!!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public Long getS3IAMConnectionForFilePathAndPattern(String bucketName, String folderPath, String filePattern,
	    String partitionedFolders) {
	Long idDataSchema = null;
	try {
	    String sql = "select idDataSchema from listDataSchema where schemaType='S3 IAMRole Batch' and folderPath=? and fileNamePattern=? and bucketName=? and partitionedFolders=? and Action='yes' order by idDataSchema desc limit 1";
	    idDataSchema = jdbcTemplate.queryForObject(sql, Long.class, folderPath, filePattern, bucketName,
		    partitionedFolders);
	} catch (EmptyResultDataAccessException e) {
	    LOG.error(
		    "\n=====> Failed in getS3IAMConnectionForFilePathAndPattern method !! - " + e.getMessage());
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in getS3IAMConnectionForFilePathAndPattern !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return idDataSchema;
    }

    @Override
    public Long getTemplateIdByPattern(long idDataSchema, String subFolderName, String fileNamePattern) {
	Long idData = null;
	try {
	    String sql = "select idData from schema_multipattern_info where idDataSchema=? and subFolderName=? and filePattern=? order by idData desc limit 1";
	    idData = jdbcTemplate.queryForObject(sql, Long.class, idDataSchema, subFolderName, fileNamePattern);
	} catch (EmptyResultDataAccessException e) {
	    LOG.error("\n=====> Failed in getTemplateIdByPattern method !! - " + e.getMessage());
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in getTemplateIdByPattern method !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return idData;
    }

    @Override
    public void saveTemplateMultiPatternInfo(long idDataSchema, long idData, String subFolderName,
	    String fileNamePattern) {
	try {
	    // Delete if there is any duplicate entry
	    String sql = "delete from schema_multipattern_info where idDataSchema=? and subFolderName=? and filePattern=?";
	    jdbcTemplate.update(sql, idDataSchema, subFolderName, fileNamePattern);

	    sql = "insert into schema_multipattern_info(idDataSchema,idData,subFolderName,filePattern) values(?,?,?,?)";
	    jdbcTemplate.update(sql, idDataSchema, idData, subFolderName, fileNamePattern);
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in saveTemplateMultiPatternInfo method !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public boolean isListDataSchemaActive(Long idDataSchema) {
	boolean status = false;
	try {
	    String sql = "select Action from listDataSchema where idDataSchema=?";
	    String activeStatus = jdbcTemplate.queryForObject(sql, String.class, idDataSchema);
	    if (activeStatus != null && activeStatus.equalsIgnoreCase("yes")) {
		status = true;
	    }
	} catch (EmptyResultDataAccessException e) {
	    LOG.error("\n=====> Failed in isListDataSchemaActive method !! - " + e.getMessage());
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in isListDataSchemaActive method !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return status;
    }

    @Override
    public boolean isTemplateActive(Long idData) {
	boolean status = false;
	try {
	    String sql = "select active from listDataSources where idData=?";
	    String activeStatus = jdbcTemplate.queryForObject(sql, String.class, idData);
	    if (activeStatus != null && activeStatus.equalsIgnoreCase("yes")) {
		status = true;
	    }
	} catch (EmptyResultDataAccessException e) {
	    LOG.error("\n=====> Failed in isTemplateActive method !! - " + e.getMessage());
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in isTemplateActive method !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return status;
    }

    @Override
    public void updateTemplateIdInMultiPatternInfo(Long idDataSchema, String subFolderName, String fileNamePattern,
	    Long idData) {
	try {
	    String sql = "update schema_multipattern_info set idData=? where idDataSchema=? and subFolderName=? and filePattern=?";
	    jdbcTemplate.update(sql, idData, idDataSchema, subFolderName, fileNamePattern);
	} catch (Exception e) {
	    LOG.error("\n=====> Failed in updateTemplateIdInMultiPatternInfo method !!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public void insertToDbkFileMonitorRules(Object[] objarr) {
	try {
	    String sql = "insert into dbk_file_monitor_rules(connection_id,validation_id,schema_name,table_name,file_indicator,"
		    + "dayofweek,hourofday,expected_time,expected_file_count,start_hour,end_hour,frequency) "
		    + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
	    jdbcTemplate.update(sql, objarr);
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public String getFileMonitorTypeByIdApp(long idApp) {
	String fileMonitorType = null;
	try {
	    String sql = "select fileMonitoringType from listApplications where idApp=?";
	    fileMonitorType = jdbcTemplate.queryForObject(sql, String.class, idApp);

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return fileMonitorType;
    }

    @Override
    public List<DBKFileMonitoringRules> getDBKFileMonitorDetailsByIdApp(long idApp) {
	List<DBKFileMonitoringRules> fileMonitoringRulesList = null;
	try {
	    String sql = "select * from dbk_file_monitor_rules where validation_id=" + idApp;

	    fileMonitoringRulesList = jdbcTemplate.query(sql, new RowMapper<DBKFileMonitoringRules>() {
		@Override
		public DBKFileMonitoringRules mapRow(ResultSet rs, int rowNum) throws SQLException {
		    DBKFileMonitoringRules fileMonitoringRules = new DBKFileMonitoringRules();
		    fileMonitoringRules.setId(rs.getInt("Id"));
		    fileMonitoringRules.setConnectionId(rs.getLong("connection_id"));
		    fileMonitoringRules.setValidationId(idApp);
		    fileMonitoringRules.setSchemaName(rs.getString("schema_name"));
		    fileMonitoringRules.setTableName(rs.getString("table_name"));
		    fileMonitoringRules.setFileIndicator(rs.getString("file_indicator"));
		    fileMonitoringRules.setDayOfWeek(rs.getString("dayOfWeek"));
		    fileMonitoringRules.setHourOfDay((Integer) rs.getObject("hourOfDay"));
		    fileMonitoringRules.setExpectedTime((Integer) rs.getObject("expected_time"));
		    fileMonitoringRules.setExpectedFileCount((Integer) rs.getObject("expected_file_count"));
		    fileMonitoringRules.setStartHour((Integer) rs.getObject("start_hour"));
		    fileMonitoringRules.setEndHour((Integer) rs.getObject("end_hour"));
		    fileMonitoringRules.setFrequency((Integer) rs.getObject("frequency"));

		    return fileMonitoringRules;
		}
	    });

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    if (fileMonitoringRulesList == null)
		fileMonitoringRulesList = new ArrayList<>();
	}
	return fileMonitoringRulesList;
    }

    @Override
    public void updateToDbkFileMonitorRules(DBKFileMonitoringRules fileMonitoringRules, long idApp) {
	try {
	    String sql = "update dbk_file_monitor_rules set schema_name=?,table_name=?,file_indicator=?,dayOfWeek=?,hourOfDay=?,expected_time=?,expected_file_count=?,start_hour=?,end_hour=?,frequency=? where validation_id="
		    + idApp + " and Id=" + fileMonitoringRules.getId();
	    jdbcTemplate.update(sql, fileMonitoringRules.getSchemaName(), fileMonitoringRules.getTableName(),
		    fileMonitoringRules.getFileIndicator(), fileMonitoringRules.getDayOfWeek(),
		    fileMonitoringRules.getHourOfDay(), fileMonitoringRules.getExpectedTime(),
		    fileMonitoringRules.getExpectedFileCount(), fileMonitoringRules.getStartHour(),
		    fileMonitoringRules.getEndHour(), fileMonitoringRules.getFrequency());
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public boolean isRowIdExitForDbkFileMonitorRules(long rowId) {
	try {
	    String sql = "select count(*) from dbk_file_monitor_rules where Id=" + rowId;
	    int count = jdbcTemplate.queryForObject(sql, Integer.class);
	    if (count > 0)
		return true;
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return false;
    }

    @Override
    public int deleteFMRule(long id) {
	try {
	    String sql = "delete from dbk_file_monitor_rules where Id=" + id;
	    jdbcTemplate.execute(sql);
	    return 1;
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return 0;
    }

    @Override
    public boolean deleteDBKFileMonitorRuleById(long id, long idApp) {
	try {
	    String sql = "delete from dbk_file_monitor_rules where validation_id=" + idApp + " and Id=" + id;
	    LOG.debug("sql=" + sql);
	    jdbcTemplate.execute(sql);
	    return true;
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return false;
    }

    @Override
    public Map<String, Object> getAzureConnectionByAccountKeyAndContainer(String account_name, String container_name,
	    String folderPath) {
	Map<String, Object> connectionAndValidation = null;
	try {

	    String sql = "select cvl.idDataSchema,cvl.idApp from (select lds.idDataSchema,fms.idApp from listDataSchema lds inner join fm_connection_details fms on lds.idDataSchema=fms.idDataSchema where lds.enableFileMonitoring='Y'"
		    + "and lds.accessKey='" + account_name + "' and lds.bucketName='" + container_name
		    + "' and lds.folderPath='" + folderPath
		    + "' and lds.Action='Yes') cvl inner join listApplications la on cvl.idApp=la.idApp where la.active='yes' order by la.idApp desc limit 1";

	    LOG.debug("SQL  =>" + sql);

	    connectionAndValidation = jdbcTemplate.queryForMap(sql);

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return connectionAndValidation;
    }

    @Override
	public long getFMValidationByIdDataSchema(long idDataSchema) {
		long idApp=0l;
		try {

			String sql = "select cvl.idApp from (select lds.idDataSchema,fms.idApp from listDataSchema lds " +
					"inner join fm_connection_details fms on lds.idDataSchema=fms.idDataSchema where lds.enableFileMonitoring='Y' " +
					"and lds.idDataSchema="+idDataSchema+" and lds.Action='Yes') cvl inner join " +
					"listApplications la on cvl.idApp=la.idApp where la.active='yes' order by la.idApp desc limit 1";

			LOG.debug("SQL  =>" + sql);

			idApp = jdbcTemplate.queryForObject(sql,Long.class);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return idApp;
	}

	@Override
    public int saveDbkFMLoadHistory(DbkFMHistory fmlh) {
	int update = 0;
	try {
	    // Query compatibility changes for both POSTGRES and MYSQL
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))
		update = jdbcTemplate1.update(
			"INSERT INTO dbk_fm_load_history_table (connection_id, validation_id,connection_type, schema_name,table_or_subfolder_name,record_count,last_load_time,file_name) "
				+ "VALUES (?,?,?,?,?,?,?::timestamp,?)",
			new Object[] { fmlh.getConnection_id(), fmlh.getValidation_id(), fmlh.getConnection_type(),
				fmlh.getSchema_name(), fmlh.getTable_or_subfolder_name(), fmlh.getRecord_count(),
				fmlh.getLast_load_time() , fmlh.getFile_name() });
	    else
		update = jdbcTemplate1.update(
			"INSERT INTO dbk_fm_load_history_table (connection_id, validation_id,connection_type, schema_name,table_or_subfolder_name,record_count,last_load_time,file_name) "
				+ "VALUES (?,?,?,?,?,?,?,?)",
			new Object[] { fmlh.getConnection_id(), fmlh.getValidation_id(), fmlh.getConnection_type(),
				fmlh.getSchema_name(), fmlh.getTable_or_subfolder_name(), fmlh.getRecord_count(),
				fmlh.getLast_load_time(), fmlh.getFile_name() });
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return update;
    }

    @Override
    public List<DBKFileMonitoringRules> getRulesListForFile(DBKFileMonitoringRules dbk_file_monitor_rules) {
	List<DBKFileMonitoringRules> rulesList = new ArrayList<DBKFileMonitoringRules>();
	try {
	    String queryForRules = "select * from dbk_file_monitor_rules where connection_id=? and validation_id=? and schema_name=? and table_name=? and dayOfWeek=? order by hourOfDay";
	    SqlRowSet resultSet = jdbcTemplate.queryForRowSet(queryForRules, dbk_file_monitor_rules.getConnectionId(),
		    dbk_file_monitor_rules.getValidationId(), dbk_file_monitor_rules.getSchemaName(),
		    dbk_file_monitor_rules.getTableName(), dbk_file_monitor_rules.getDayOfWeek());
	    dbk_file_monitor_rules = null;
	    while (resultSet.next()) {
		dbk_file_monitor_rules = new DBKFileMonitoringRules();
		dbk_file_monitor_rules.setConnectionId(resultSet.getLong("connection_id"));
		dbk_file_monitor_rules.setValidationId(resultSet.getLong("validation_id"));
		dbk_file_monitor_rules.setSchemaName(resultSet.getString("schema_name"));
		dbk_file_monitor_rules.setTableName(resultSet.getString("table_name"));
		dbk_file_monitor_rules.setDayOfWeek(resultSet.getString("dayOfWeek"));
		dbk_file_monitor_rules.setFileIndicator(resultSet.getString("file_indicator"));
		dbk_file_monitor_rules.setHourOfDay(resultSet.getInt("hourOfDay"));
		dbk_file_monitor_rules.setExpectedTime(resultSet.getInt("expected_time"));
		dbk_file_monitor_rules.setExpectedFileCount(resultSet.getInt("expected_file_count"));
		dbk_file_monitor_rules.setStartHour(resultSet.getInt("start_hour"));
		dbk_file_monitor_rules.setEndHour(resultSet.getInt("end_hour"));
		dbk_file_monitor_rules.setFrequency(resultSet.getInt("frequency"));
		rulesList.add(dbk_file_monitor_rules);
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return rulesList;
    }

    @Override
    public int getArrivalCountOfFile(DBKFileMonitoringRules dbk_file_monitor_rules, String load_date, int loaded_hour) {
	int count = 0;
	// Query compatibility changes for both POSTGRES and MYSQL

	String query = "";
	if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {

	    query = "select count(*) from dbk_fm_filearrival_details where connection_id=? and validation_id=? and schema_name=? and table_or_subfolder_name=? and load_date =?::Date and loaded_hour =?";
	} else {
	    query = "select count(*) from dbk_fm_filearrival_details where connection_id=? and validation_id=? and schema_name=? and table_or_subfolder_name=? and load_date =? and loaded_hour =?";

	}
	try {
	    count = jdbcTemplate1.queryForObject(query, Integer.class, dbk_file_monitor_rules.getConnectionId(),
		    dbk_file_monitor_rules.getValidationId(), dbk_file_monitor_rules.getSchemaName(),
		    dbk_file_monitor_rules.getTableName(), load_date, loaded_hour);
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return count;
    }

    @Override
    public long saveDbkFMFileArrivalDetails(DbkFMFileArrivalDetails fmad) {
	long file_arrival_id = 0l;
	try {

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String sql = "";
	    if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
		sql = "INSERT INTO dbk_fm_filearrival_details (connection_id, validation_id, schema_name,table_or_subfolder_name,file_indicator,dayOfWeek,load_date,loaded_hour,loaded_time,expected_hour,expected_time,size_or_record_count,"
			+ " size_or_record_count_check,column_metadata_check,file_validity_status,file_arrival_status,file_name) "
			+ "VALUES (?,?,?,?,?,?,?::Date,?,?,?,?,?,?,?,?,?,?)";
	    } else {
		sql = "INSERT INTO dbk_fm_filearrival_details (connection_id, validation_id, schema_name,table_or_subfolder_name,file_indicator,dayOfWeek,load_date,loaded_hour,loaded_time,expected_hour,expected_time,size_or_record_count,"
			+ " size_or_record_count_check,column_metadata_check,file_validity_status,file_arrival_status,file_name) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	    }

	    String query = sql;

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "id" : "Id";
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    jdbcTemplate1.update(new PreparedStatementCreator() {
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    PreparedStatement pst = con.prepareStatement(query, new String[] { key_name });
		    pst.setLong(1, fmad.getConnection_id());
		    pst.setLong(2, fmad.getValidation_id());
		    pst.setString(3, fmad.getSchema_name());
		    pst.setString(4, fmad.getTable_or_subfolder_name());
		    pst.setString(5, fmad.getFile_indicator());
		    pst.setString(6, fmad.getDayOfWeek());
		    pst.setString(7, fmad.getLoad_date());
		    pst.setObject(8, fmad.getLoaded_hour());
		    pst.setObject(9, fmad.getLoaded_time());
		    pst.setObject(10, fmad.getExpected_hour());
		    pst.setObject(11, fmad.getExpected_time());
		    pst.setInt(12, fmad.getRecord_count());
		    pst.setString(13, fmad.getRecord_count_check());
		    pst.setString(14, fmad.getColumn_metadata_check());
		    pst.setString(15, fmad.getFile_validity_status());
		    pst.setString(16, fmad.getFile_arrival_status());
		    pst.setString(17, fmad.getFileName());
		    return pst;
		}
	    }, keyHolder);

	    file_arrival_id = keyHolder.getKey().longValue();
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return file_arrival_id;
    }

    @Override
    public Long getTemplateIdOfConnectionByTableName(long idDataSchema, String tableName) {
	Long templateId = null;
	try {
	    String sql = "select t1.idData from listDataSources t1 join listDataAccess t2 on t1.idData=t2.idData and t1.active='yes' and t1.idDataSchema=? and t2.folderName=? order by t1.idData desc limit 1";
	    List<Long> templateIdList = jdbcTemplate.queryForList(sql, Long.class, idDataSchema, tableName);
	    if (templateIdList != null && templateIdList.size() != 0)
		templateId = templateIdList.get(0);

	} catch (Exception e) {
	    LOG.error("\n=====> Failed to get Datasource for selected Connection and tableName !!!!");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return templateId;
    }

    @Override
    public Long getIncrementalDQApplicationsForIdData(Long idData) {
	Long idApp = 0L;
	try {
	    String sql = "select max(idApp) as idApp from listApplications where active='yes' and appType='Data Forensics' and keyGroupRecordCountAnomaly='N' and incrementalMatching='Y' and buildHistoricFingerPrint='N' and idData="
		    + idData;
	    SqlRowSet queryForRowSet = jdbcTemplate.queryForRowSet(sql);
	    while (queryForRowSet.next()) {
		idApp = queryForRowSet.getLong("idApp");
	    }
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return idApp;
    }

    @Override
    public ListDataSource getDataSourceForRowAdd(Long idData) {
	try {
	    RowMapper<ListDataSource> rm = (rs, i) -> {
		ListDataSource lds = new ListDataSource();
		lds.setIdData(rs.getInt("idData"));
		lds.setName(rs.getString("name"));
		lds.setDescription(rs.getString("description"));

		DataSourceType dataSourceType = DataSourceType
			.valueOf(rs.getString("dataLocation").toUpperCase().replaceAll("\\s+", ""));
		lds.setDataLocation(dataSourceType.toString());
		lds.setDataSource(rs.getString("dataSource"));
		lds.setCreatedBy(rs.getLong("createdBy"));
		lds.setIdDataBlend(rs.getInt("idDataBlend"));
		lds.setCreatedAt(rs.getDate("createdAt"));
		lds.setUpdatedAt(rs.getDate("updatedAt"));
		lds.setUpdatedBy(rs.getLong("updatedBy"));
		lds.setCreatedByUser(rs.getString("createdByUser"));
		lds.setSchemaName(rs.getString("schemaName"));
		lds.setIdDataSchema(rs.getLong("idDataSchema"));
		lds.setIgnoreRowsCount(rs.getLong("ignoreRowsCount"));
		lds.setProfilingEnabled(rs.getString("profilingEnabled"));
		lds.setAdvancedRulesEnabled(rs.getString("advancedRulesEnabled"));
		lds.setTemplateCreateSuccess(rs.getString("template_create_success"));
		lds.setDeltaApprovalStatus(rs.getString("deltaApprovalStatus"));
		lds.setProjectId(rs.getInt("project_id"));
		lds.setDomain((rs.getInt("domain_id")));
		return lds;
	    };
	    String sql = "select * from listDataSources where  idData=?";
	    return jdbcTemplate.query(sql, rm, idData).get(0);
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    @Override
    public void updateIncrementalColStatus(long idData, String displayName) {

	try {
	    // update listDataDefinition
	    String sql = "update listDataDefinition set incrementalCol='Y' where idData=? and displayName=?";
	    jdbcTemplate.update(sql, idData, displayName);

	    // update staging_listDataDefinition
	    sql = "update staging_listDataDefinition set incrementalCol='Y' where idData=? and displayName=?";
	    jdbcTemplate.update(sql, idData, displayName);

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();

	}
    }

    @Override
    public long insertintolistapplications(String name, String description, String apptype, Long idData, Long idUser,
	    Double matchingThreshold, String incrementalMatching, String dateFormat, String leftSliceEnd,
	    String fileMonitoringType, String continuousFileMonitoring, Long project_id, String createdByUser,
	    Long domainId) {

//		validationCheckName, "A default validation check",
//		"Data Forensics", idData, idUser, 
//		0.0, "N", null, null, null,null, createdByUser,

	// LOG.debug("idDataSchema:"+idDataSchema);
	try {
	    String sql = "INSERT INTO listApplications "
		    + "(name,description,apptype,idData,createdBy,createdAt,updatedAt,updatedBy,fileNameValidation,entityColumn,colOrderValidation,"
		    + "matchingThreshold,nonNullCheck,numericalStatCheck,stringStatCheck,recordAnomalyCheck,incrementalMatching,historicDateFormat,fileMonitoringType,continuousFileMonitoring, project_id,"
		    + "createdByUser,validityThreshold,domain_id) VALUES (?,?,?,?,?,now(),now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	    // Query compatibility changes for both POSTGRES and MYSQL
	    String key_name = (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) ? "idapp"
		    : "idApp";

	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    jdbcTemplate.update(new PreparedStatementCreator() {
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		    PreparedStatement pst = con.prepareStatement(sql, new String[] { key_name });
		    pst.setString(1, name);
		    pst.setString(2, description);
		    pst.setString(3, apptype);
		    pst.setLong(4, idData);
		    pst.setLong(5, idUser);
		    pst.setLong(6, idUser);
		    pst.setString(7, " ");
		    pst.setString(8, " ");
		    pst.setString(9, " ");
		    pst.setDouble(10, matchingThreshold);
		    pst.setString(11, "N");
		    pst.setString(12, "N");
		    pst.setString(13, "N");
		    pst.setString(14, "N");
		    pst.setString(15, incrementalMatching);
		    pst.setString(16, dateFormat);
		    pst.setString(17, fileMonitoringType);
		    pst.setString(18, continuousFileMonitoring);
		    pst.setLong(19, project_id);
		    pst.setString(20, createdByUser);
		    pst.setDouble(21, 1.0);
		    pst.setLong(22, domainId);
		    return pst;
		}
	    }, keyHolder);
	    return keyHolder.getKey().longValue();
	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return 0l;
    }

    @Override
    public void updateIncrementalMatchingOfApplications(long idApp, String incrementalMatching) {
	try {
	    String sql = " update listApplications set  incrementalMatching=? where idApp=?";
	    jdbcTemplate.update(sql, incrementalMatching, idApp);
	} catch (Exception e) {
	    LOG.error("\n====> Exception occurred while updating incrementalMatching of validation to ["
		    + incrementalMatching + "]");
	    LOG.error(e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public Map<String, Object> getListDataSchemaDeatilsForidData(Long idData) {
	Map<String, Object> listDataSchemaDeatils = null;
	try {

	    String sql = "select schemaType,createdBy,project_id,createdByUser from listDataSchema where idDataSchema ="
		    + idData;

	    LOG.debug("SQL  =>" + sql);

	    listDataSchemaDeatils = jdbcTemplate.queryForMap(sql);

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return listDataSchemaDeatils;

    }

    @Override
    public Map<String, Object> getS3BucketConnectionDetailsByName(String bucketName, String schemaType,
	    String folderPath) {
	Map<String, Object> connectionAndValidation = null;
	try {

	    String sql = "select cvl.idDataSchema,cvl.idApp from (select lds.idDataSchema,fms.idApp from listDataSchema lds inner join fm_connection_details fms on lds.idDataSchema=fms.idDataSchema where lds.enableFileMonitoring='Y'"
		    + "and lds.bucketName='" + bucketName + "' and lds.schemaType='" + schemaType
		    + "' and lds.folderPath='" + folderPath
		    + "' and lds.Action='Yes') cvl inner join listApplications la on cvl.idApp=la.idApp where la.active='yes' order by la.idApp desc limit 1";

	    LOG.debug("sql=" + sql);

	    connectionAndValidation = jdbcTemplate.queryForMap(sql);

	} catch (Exception e) {
		LOG.error(e.getMessage());
	    e.printStackTrace();
	}
	return connectionAndValidation;
    }

	@Override
	public boolean isDuplicateFMRuleForHourlyFiles(DBKFileMonitoringRules dbkFileMonitoringRules) {
		boolean isFMRuleDuplicate= false;
		try{
			String sql="select count(*) from dbk_file_monitor_rules where connection_id=? and validation_id=? and " +
					"schema_name=? and table_name=? and file_indicator=? and dayofweek=? and hourofday=? and expected_time=?";
			int count = jdbcTemplate.queryForObject(sql,Integer.class,dbkFileMonitoringRules.getConnectionId(),dbkFileMonitoringRules.getValidationId(),
					dbkFileMonitoringRules.getSchemaName(),dbkFileMonitoringRules.getTableName(),dbkFileMonitoringRules.getFileIndicator(),
					dbkFileMonitoringRules.getDayOfWeek(),dbkFileMonitoringRules.getHourOfDay(),dbkFileMonitoringRules.getExpectedTime());
			if(count > 0)
				isFMRuleDuplicate = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return isFMRuleDuplicate;
	}
}
