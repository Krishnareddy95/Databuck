package com.databuck.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.databuck.aver.bean.ABCCheckFailedReport;
import com.databuck.aver.bean.ABCCheckPerformanceSummary;
import com.databuck.aver.bean.ABCCheckRuleSummaryReport;
import com.databuck.aver.bean.ABCCheckSummaryReport;
import com.databuck.aver.bean.AVERDashboardSummary;
import com.databuck.aver.bean.AVERPerformanceSummary;
import com.databuck.aver.bean.FMFilePerformanceSummary;
import com.databuck.aver.bean.FileCheckReport;
import com.databuck.aver.bean.TrendCheckDataDriftReport;
import com.databuck.aver.bean.TrendCheckFailedReport;
import com.databuck.aver.bean.TrendCheckMicrosegmentReport;
import com.databuck.aver.bean.TrendCheckPerformanceSummary;
import com.databuck.aver.bean.TrendCheckSummaryReport;
import com.databuck.aver.bean.TrendCheckTrendGraphReport;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.dao.AverSummaryDao;

@Repository
public class AverSummaryDaoImpl implements AverSummaryDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;
	
	private static final Logger LOG = Logger.getLogger(AverSummaryDaoImpl.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public AVERDashboardSummary getFileMonitoringSummaryForDateRange(String startDate, String endDate) {
		AVERDashboardSummary fileMonitorSumry = new AVERDashboardSummary();
		fileMonitorSumry.setSummaryName("FILE MONITORING");
		try {
			// Get validations list
			String appListSql = "Select t1.idApp from locationMapping t1 join locations t2 on t1.locationId=t2.id and t2.locationName='FileMonitoring'";
			List<Long> appList = jdbcTemplate.query(appListSql, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long idApp = rs.getLong("idApp");
					return idApp;
				}
			});

			if (appList != null && appList.size() > 0) {
				String idAppListStr = "";
				for (Long idApp : appList) {
					idAppListStr = idAppListStr + idApp + ",";
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				// Get total files count
				String totalCountSql = "Select count(*) from file_tracking_history where date>='" + startDate
						+ "' and date<='" + endDate + "' and idApp in (" + idAppListStr + ")";
				Integer totalCount = jdbcTemplate.queryForObject(totalCountSql, Integer.class);

				if (totalCount != null) {

					// Get the pass and fail count and percentage
					String statusCountSql = "select status, count(*) as groupCount, (count(*)*100/" + totalCount
							+ ") as percentage from file_tracking_history where date>='" + startDate + "' and date<='"
							+ endDate + "' and idApp in (" + idAppListStr + ") group by status";

					LOG.debug("FM statusCountSql: " + statusCountSql);

					List<Map<String, Object>> statusList = jdbcTemplate.queryForList(statusCountSql);

					if (statusList != null && statusList.size() > 0) {
						for (Map<String, Object> statusMap : statusList) {
							if (statusMap != null && statusMap.size() == 3) {
								String status = (String) statusMap.get("status");

								Long groupCount = (Long) statusMap.get("groupCount");
								if (groupCount == null) {
									groupCount = 0l;
								}

								BigDecimal statusPerc = (BigDecimal) statusMap.get("percentage");
								double percentage = 0.0;
								if (statusPerc != null) {
									percentage = statusPerc.doubleValue();
								}

								if (status != null && status.trim().equalsIgnoreCase("passed")) {
									fileMonitorSumry.setPassedCount(groupCount);
									fileMonitorSumry.setPassPercentage(percentage);
								} else if (status != null && status.trim().equalsIgnoreCase("failed")) {
									fileMonitorSumry.setFailedCount(groupCount);
									fileMonitorSumry.setFailPercentage(percentage);
								}

							}
						}
					}
				} else {
					totalCount = 0;
				}
				fileMonitorSumry.setTotalCount(totalCount);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return fileMonitorSumry;
	}

	@Override
	public AVERDashboardSummary getABCCheckSummaryForDateRange(String startDate, String endDate) {
		AVERDashboardSummary abcCheckSumry = new AVERDashboardSummary();
		abcCheckSumry.setSummaryName("ABC & DOMAIN");
		try {
			// Get validations list
			String appListSql = "Select t1.idApp from locationMapping t1 join locations t2 on t1.locationId=t2.id and t2.locationName='ABCCheck'";
			List<Long> appList = jdbcTemplate.query(appListSql, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long idApp = rs.getLong("idApp");
					return idApp;
				}
			});

			if (appList != null && appList.size() > 0) {
				int totalCount = 0;
				int passCount = 0;
				int failCount = 0;

				String idAppListStr = "";
				for (Long idApp : appList) {
					idAppListStr = idAppListStr + idApp + ",";
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				String sql = "select sum(m4.DQI=100) as passCount, sum(m4.DQI<100) as failCount from (select m3.AppId,m3.date,m3.Run, Avg(m3.DQI) as DQI from (select m2.AppId, m2.date, m2.Run,case when (m1.DQI IS NULL) then 0 else m1.DQI end as DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where  AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.DQI is not null) m3 group by m3.AppId,m3.date,m3.Run) m4;";
				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passCount = pCount.intValue();
					}
					if (fCount != null) {
						failCount = fCount.intValue();
					}
					totalCount = passCount + failCount;
				}

				Double totalPassPerc = ((double) passCount / totalCount) * 100;
				if (totalPassPerc == null || totalPassPerc.isNaN()) {
					totalPassPerc = 0.0;
				}
				Double totalFailPerc = ((double) failCount / totalCount) * 100;
				if (totalFailPerc == null || totalFailPerc.isNaN()) {
					totalFailPerc = 0.0;
				}

				abcCheckSumry.setTotalCount(totalCount);
				abcCheckSumry.setPassedCount(passCount);
				abcCheckSumry.setPassPercentage(totalPassPerc);
				abcCheckSumry.setFailedCount(failCount);
				abcCheckSumry.setFailPercentage(totalFailPerc);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return abcCheckSumry;
	}

	@Override
	public AVERDashboardSummary getTrendCheckSummaryForDateRange(String startDate, String endDate) {
		AVERDashboardSummary trendSumry = new AVERDashboardSummary();
		trendSumry.setSummaryName("TREND CHECK");
		try {
			// Get validations list
			String appListSql = "Select t1.idApp from locationMapping t1 join locations t2 on t1.locationId=t2.id and t2.locationName='TrendCheck'";
			List<Long> appList = jdbcTemplate.query(appListSql, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long idApp = rs.getLong("idApp");
					return idApp;
				}
			});

			if (appList != null && appList.size() > 0) {
				int totalCount = 0;
				int passCount = 0;
				int failCount = 0;

				String idAppListStr = "";
				for (Long idApp : appList) {
					idAppListStr = idAppListStr + idApp + ",";
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				String sql = "Select sum(m3.DQI=100) as passCount, sum(m3.DQI<100) as failCount from (select m2.AppId, m2.date, m2.Run,m1.DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.Test='DQ_Numerical Field Fingerprint' and m1.DQI is not null) m3";
				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passCount = pCount.intValue();
					}
					if (fCount != null) {
						failCount = fCount.intValue();
					}
					totalCount = passCount + failCount;
				}

				Double totalPassPerc = ((double) passCount / totalCount) * 100;
				if (totalPassPerc == null || totalPassPerc.isNaN()) {
					totalPassPerc = 0.0;
				}
				Double totalFailPerc = ((double) failCount / totalCount) * 100;
				if (totalFailPerc == null || totalFailPerc.isNaN()) {
					totalFailPerc = 0.0;
				}
				trendSumry.setTotalCount(totalCount);
				trendSumry.setPassedCount(passCount);
				trendSumry.setPassPercentage(totalPassPerc);
				trendSumry.setFailedCount(failCount);
				trendSumry.setFailPercentage(totalFailPerc);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return trendSumry;
	}

	@Override
	public AVERDashboardSummary getABCCheckSummaryForSourceTypeDateRange(String startDate, String endDate,
			String sourceType) {
		AVERDashboardSummary abcCheckSumry = new AVERDashboardSummary();
		abcCheckSumry.setSummaryName("ABC & DOMAIN");
		try {
			// Get validations list
			String appListSql = "Select t1.idApp from locationMapping t1 join locations t2 on t1.locationId=t2.id and t2.locationName='ABCCheck' and t1.sourceType='"
					+ sourceType + "'";
			List<Long> appList = jdbcTemplate.query(appListSql, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long idApp = rs.getLong("idApp");
					return idApp;
				}
			});

			if (appList != null && appList.size() > 0) {
				int totalCount = 0;
				int passCount = 0;
				int failCount = 0;

				String idAppListStr = "";
				for (Long idApp : appList) {
					idAppListStr = idAppListStr + idApp + ",";
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				String sql = "select sum(m4.DQI=100) as passCount, sum(m4.DQI<100) as failCount from (select m3.AppId,m3.date,m3.Run, Avg(m3.DQI) as DQI from (select m2.AppId, m2.date, m2.Run,case when (m1.DQI IS NULL) then 0 else m1.DQI end as DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where  AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.DQI is not null) m3 group by m3.AppId,m3.date,m3.Run) m4;";
				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passCount = pCount.intValue();
					}
					if (fCount != null) {
						failCount = fCount.intValue();
					}
					totalCount = passCount + failCount;
				}

				Double totalPassPerc = ((double) passCount / totalCount) * 100;
				if (totalPassPerc == null || totalPassPerc.isNaN()) {
					totalPassPerc = 0.0;
				}
				Double totalFailPerc = ((double) failCount / totalCount) * 100;
				if (totalFailPerc == null || totalFailPerc.isNaN()) {
					totalFailPerc = 0.0;
				}

				abcCheckSumry.setTotalCount(totalCount);
				abcCheckSumry.setPassedCount(passCount);
				abcCheckSumry.setPassPercentage(totalPassPerc);
				abcCheckSumry.setFailedCount(failCount);
				abcCheckSumry.setFailPercentage(totalFailPerc);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return abcCheckSumry;
	}

	@Override
	public AVERDashboardSummary getTrendCheckSummaryForSourceTypeDateRange(String startDate, String endDate,
			String sourceType) {
		AVERDashboardSummary trendSumry = new AVERDashboardSummary();
		trendSumry.setSummaryName("TREND CHECK");
		try {
			// Get validations list
			String appListSql = "Select t1.idApp from locationMapping t1 join locations t2 on t1.locationId=t2.id and t2.locationName='TrendCheck' and t1.sourceType='"
					+ sourceType + "'";
			List<Long> appList = jdbcTemplate.query(appListSql, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Long idApp = rs.getLong("idApp");
					return idApp;
				}
			});

			if (appList != null && appList.size() > 0) {
				int totalCount = 0;
				int passCount = 0;
				int failCount = 0;

				String idAppListStr = "";
				for (Long idApp : appList) {
					idAppListStr = idAppListStr + idApp + ",";
				}
				idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

				String sql = "Select sum(m3.DQI=100) as passCount, sum(m3.DQI<100) as failCount from (select m2.AppId, m2.date, m2.Run,m1.DQI from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.Test='DQ_Numerical Field Fingerprint' and m1.DQI is not null) m3";
				LOG.debug("Sql:" + sql);

				Map<String, Object> statusMap = jdbcTemplate1.queryForMap(sql);
				if (statusMap != null && statusMap.size() > 0) {
					BigDecimal pCount = (BigDecimal) statusMap.get("passCount");
					BigDecimal fCount = (BigDecimal) statusMap.get("failCount");
					if (pCount != null) {
						passCount = pCount.intValue();
					}
					if (fCount != null) {
						failCount = fCount.intValue();
					}
					totalCount = passCount + failCount;
				}

				Double totalPassPerc = ((double) passCount / totalCount) * 100;
				if (totalPassPerc == null || totalPassPerc.isNaN()) {
					totalPassPerc = 0.0;
				}
				Double totalFailPerc = ((double) failCount / totalCount) * 100;
				if (totalFailPerc == null || totalFailPerc.isNaN()) {
					totalFailPerc = 0.0;
				}
				trendSumry.setTotalCount(totalCount);
				trendSumry.setPassedCount(passCount);
				trendSumry.setPassPercentage(totalPassPerc);
				trendSumry.setFailedCount(failCount);
				trendSumry.setFailPercentage(totalFailPerc);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return trendSumry;
	}

	@Override
	public Double getTrendCheckDQIOfIdAPPForDate(Long idApp, String trend_Date) {
		Double dqi = null;
		;
		try {
			String sql = "select DQI from DashBoard_Summary where Test = 'DQ_Numerical Field Fingerprint' and AppId="
					+ idApp + " and Date='" + trend_Date
					+ "' and Run = (select MAX(Run) from DashBoard_Summary where Test = 'DQ_Numerical Field Fingerprint' and AppId = "
					+ idApp + " " + "and Date = '" + trend_Date + "')";
			LOG.debug(sql);
			dqi = jdbcTemplate1.queryForObject(sql, Double.class);
			LOG.debug("TrendCheckDQI: " + dqi);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return dqi;
	}

	@Override
	public List<FileCheckReport> getfmFailedFilesReportForDateRange(String startDate, String endDate) {
		List<FileCheckReport> result = new ArrayList<FileCheckReport>();
		try {
			String sql = "Select * from file_tracking_history where date>='" + startDate + "' and date<='" + endDate
					+ "' and status='failed'";
			result = jdbcTemplate.query(sql, new RowMapper<FileCheckReport>() {

				@Override
				public FileCheckReport mapRow(ResultSet rs, int rowNum) throws SQLException {
					FileCheckReport fileCheckReport = new FileCheckReport();
					fileCheckReport.setFolderPath(rs.getString("folderPath"));
					fileCheckReport.setFileName(rs.getString("fileName"));
					fileCheckReport.setOverallStatus(rs.getString("status"));
					fileCheckReport.setZeroSizeFileCheck(rs.getString("zeroSizeFileCheck"));
					fileCheckReport.setColumnCountCheck(rs.getString("columnCountCheck"));
					fileCheckReport.setColumnSequenceCheck(rs.getString("columnSequenceCheck"));
					fileCheckReport.setRecordLengthCheck(rs.getString("recordLengthCheck"));
					fileCheckReport.setRecordMaxLengthCheck(rs.getString("recordMaxLengthCheck"));
					return fileCheckReport;
				}

			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public FileCheckReport getFMSummaryDetailsOfFile(String processedDate, String sourceType, String source,
			String fileName) {

		FileCheckReport result = null;

		try {
			String sql = "select t1.* from file_tracking_history t1 join locationMapping t2 on t1.idApp=t2.idApp and t1.date=? and t2.sourceType=? and t2.source=? and t2.fileName=? order by t1.id desc limit 1";
			List<FileCheckReport> reportList = jdbcTemplate.query(sql, new RowMapper<FileCheckReport>() {

				@Override
				public FileCheckReport mapRow(ResultSet rs, int rowNum) throws SQLException {
					FileCheckReport fileCheckReport = new FileCheckReport();
					fileCheckReport.setFolderPath(rs.getString("folderPath"));
					fileCheckReport.setFileName(rs.getString("fileName"));
					fileCheckReport.setOverallStatus(rs.getString("status"));
					fileCheckReport.setZeroSizeFileCheck(rs.getString("zeroSizeFileCheck"));
					fileCheckReport.setColumnCountCheck(rs.getString("columnCountCheck"));
					fileCheckReport.setColumnSequenceCheck(rs.getString("columnSequenceCheck"));
					fileCheckReport.setRecordLengthCheck(rs.getString("recordLengthCheck"));
					fileCheckReport.setRecordMaxLengthCheck(rs.getString("recordMaxLengthCheck"));
					return fileCheckReport;
				}

			}, processedDate, sourceType, source, fileName);

			if (reportList != null && reportList.size() > 0) {
				result = reportList.get(0);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;

	}

	@Override
	public List<String> getUniqueTypeListFromLocMap() {
		List<String> typeList = new ArrayList<String>();
		try {
			String sql = "select distinct sourceType from locationMapping";
			typeList = jdbcTemplate.queryForList(sql, String.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return typeList;
	}

	@Override
	public Map<Integer, String> getUniqueLocationsList() {
		Map<Integer, String> locationList = new HashMap<Integer, String>();
		try {
			String sql = "select id,locationName from locations";
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

			if (resultList != null) {
				for (Map<String, Object> rowMap : resultList) {
					if (rowMap != null) {
						Integer locationId = (Integer) rowMap.get("id");
						String locationName = (String) rowMap.get("locationName");
						locationList.put(locationId, locationName);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return locationList;
	}

	@Override
	public List<Long> getValdiationsForSourceFileAndLocation(String sourceType, String source, String fileName,
			Integer locationId) {
		List<Long> validationList = new ArrayList<Long>();
		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=?";
			validationList = jdbcTemplate.queryForList(sql, Long.class, sourceType, source, fileName, locationId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationList;
	}

	@Override
	public List<Long> getValdiationsForLocation(Integer locationId) {
		List<Long> validationList = new ArrayList<Long>();
		try {
			String sql = "select distinct idApp from locationMapping where locationId=?";
			validationList = jdbcTemplate.queryForList(sql, Long.class, locationId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationList;
	}

	@Override
	public Date getMaxDateForFMValidation(Long idApp, String startDate, String endDate) {
		String query = "select Max(Date) from file_tracking_history where idApp=? and Date>='" + startDate
				+ "' and Date<='" + endDate + "'";
		Date maxDate = null;
		try {
			maxDate = jdbcTemplate.queryForObject(query, Date.class, idApp);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return maxDate;
	}

	public List<FileTrackingHistory> getFMResultsForDate(Long idApp, Date maxDate) {
		List<FileTrackingHistory> historyList = null;
		try {
			if (maxDate != null) {
				String mDate = sdf.format(maxDate);
				String query = "select * from file_tracking_history where date='" + mDate + "' and idApp = " + idApp
						+ " order by id desc limit 1";

				LOG.debug("query: " + query);
				historyList = jdbcTemplate.query(query, new RowMapper<FileTrackingHistory>() {
					@Override
					public FileTrackingHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
						FileTrackingHistory fileTrackingHistory = new FileTrackingHistory();
						fileTrackingHistory.setFileFormat(rs.getString("fileFormat"));
						fileTrackingHistory.setFileName(rs.getString("fileName"));
						fileTrackingHistory.setFolderPath(rs.getString("folderPath"));
						fileTrackingHistory.setStatus(rs.getString("status"));
						fileTrackingHistory.setColumnCountCheck(rs.getString("columnCountCheck"));
						fileTrackingHistory.setColumnSequenceCheck(rs.getString("columnSequenceCheck"));
						fileTrackingHistory.setZeroSizeFileCheck(rs.getString("zeroSizeFileCheck"));
						fileTrackingHistory.setRecordLengthCheck(rs.getString("recordLengthCheck"));
						fileTrackingHistory.setRecordMaxLengthCheck(rs.getString("recordMaxLengthCheck"));
						return fileTrackingHistory;
					}
				});
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return historyList;
	}

	@Override
	public List<String> getSourceListForSourceType(String sourceType) {
		List<String> sourceList = new ArrayList<String>();
		try {
			String sql = "select distinct source from locationMapping where sourceType=?";
			sourceList = jdbcTemplate.queryForList(sql, String.class, sourceType);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return sourceList;
	}

	@Override
	public Integer getLocationIdByName(String locationName) {
		Integer locationId = 0;
		try {
			String sql = "select id from locations where locationName='" + locationName + "'";
			locationId = jdbcTemplate.queryForObject(sql, Integer.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return locationId;
	}

	@Override
	public Map<String, Long> getFileCountSummary(List<Long> validationList, String startDate, String endDate) {
		Map<String, Long> resultMap = new HashMap<String, Long>();
		try {

			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the total files count
			Long totalFilesCount = 0l;
			String totalCountSql = "select sum(fileCount) as totalFileCount from file_monitor_rules where idApp in ("
					+ idAppListStr + ")";
			totalFilesCount = jdbcTemplate.queryForObject(totalCountSql, Long.class);
			if (totalFilesCount == null) {
				totalFilesCount = 0l;
			}
			// Get the processed,pass and fail files count
			String statusCountSql = "select status, count(*) as groupCount from file_tracking_history where idApp in ("
					+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate + "' group by status";

			LOG.debug("statusCountSql: " + statusCountSql);

			List<Map<String, Object>> statusList = jdbcTemplate.queryForList(statusCountSql);
			long passedFilesCount = 0l;
			long failedFilesCount = 0l;
			long processedFilesCount = 0l;

			if (statusList != null && statusList.size() > 0) {
				for (Map<String, Object> statusMap : statusList) {
					if (statusMap != null && statusMap.size() > 0) {
						String status = (String) statusMap.get("status");

						Long groupCount = (Long) statusMap.get("groupCount");
						if (groupCount == null) {
							groupCount = 0l;
						}

						if (status != null && status.trim().equalsIgnoreCase("passed")) {
							passedFilesCount = passedFilesCount + groupCount;
						} else if (status != null && status.trim().equalsIgnoreCase("failed")) {
							failedFilesCount = failedFilesCount + groupCount;
						}
						processedFilesCount = processedFilesCount + groupCount;
					}
				}
			}

			resultMap.put("totalFilesCount", totalFilesCount);
			resultMap.put("processedFilesCount", processedFilesCount);
			resultMap.put("passedFilesCount", passedFilesCount);
			resultMap.put("failedFilesCount", failedFilesCount);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Double getTotalDQIOfIdAPPForDate(Long idApp, String maxDate) {
		Double totalDQI = null;
		try {
			String sql = "";
			if (maxDate != null) {
				sql = "select Avg(DQI) from DashBoard_Summary where AppId=" + idApp + " and Date='" + maxDate
						+ "' and Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp + " "
						+ "and Date = '" + maxDate + "')";
			} else {
				sql = "select Avg(DQI) from DashBoard_Summary where AppId=" + idApp
						+ " and Date=(select max(Date) from DashBoard_Summary where AppId=" + idApp
						+ ") and Run = (select MAX(Run) from DashBoard_Summary where AppId = " + idApp
						+ "and Date = (select max(Date) from DashBoard_Summary where AppId=" + idApp + "))";
			}

			totalDQI = jdbcTemplate1.queryForObject(sql, Double.class);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return totalDQI;
	}

	@Override
	public Date getMaxDateForValidation(Long idApp, String startDate, String endDate) {
		Date maxDate = null;
		try {
			String sql = "Select max(Date) from DATA_QUALITY_Transactionset_sum_A1 where Date>='" + startDate
					+ "' and Date<='" + endDate + "' and idApp=?";
			maxDate = jdbcTemplate1.queryForObject(sql, Date.class, idApp);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return maxDate;
	}

	@Override
	public Map<String, Map<String, Long>> getFMPerformanceSummary(List<Long> validationList, String startDate,
			String endDate) {
		Map<String, Map<String, Long>> resultMap = new LinkedHashMap<String, Map<String, Long>>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Select 10 distinct dates in descending order
			String dateSql = "select distinct date from file_tracking_history where idApp in (" + idAppListStr
					+ ") and date<='" + endDate + "' order by date desc limit 10";
			List<Date> dateList = jdbcTemplate.queryForList(dateSql, Date.class);
			String dateListStr = "";
			for (Date dt : dateList) {
				String f_date = sdf.format(dt);
				dateListStr = dateListStr + "'" + f_date + "',";
			}
			dateListStr = dateListStr.substring(0, dateListStr.length() - 1);

			// Get the processed,pass and fail files count for each date
			String statusCountSql = "select date, status, count(*) as groupCount from file_tracking_history where idApp in ("
					+ idAppListStr + ") and date in (" + dateListStr + ") group by date,status";

			List<Map<String, Object>> statusList = jdbcTemplate.queryForList(statusCountSql);

			if (statusList != null && statusList.size() > 0) {
				for (Map<String, Object> statusMap : statusList) {
					if (statusMap != null && statusMap.size() > 0) {
						long passedFilesCount = 0l;
						long failedFilesCount = 0l;
						long processedFilesCount = 0l;

						Date groupDate = (Date) statusMap.get("date");
						String g_date = sdf.format(groupDate);

						String status = (String) statusMap.get("status");

						Long groupCount = (Long) statusMap.get("groupCount");
						if (groupCount == null) {
							groupCount = 0l;
						}

						// Check if the group of date already exists
						Map<String, Long> groupMap = null;

						if (resultMap.containsKey(g_date)) {
							groupMap = resultMap.get(g_date);
						} else {
							groupMap = new HashMap<String, Long>();
						}

						if (status != null && status.trim().equalsIgnoreCase("passed")) {
							passedFilesCount = groupCount;

							if (groupMap.containsKey("passedFilesCount")) {
								passedFilesCount = passedFilesCount + groupMap.get("passedFilesCount");
							}
							groupMap.put("passedFilesCount", passedFilesCount);

						} else if (status != null && status.trim().equalsIgnoreCase("failed")) {
							failedFilesCount = groupCount;
							if (groupMap.containsKey("failedFilesCount")) {
								failedFilesCount = failedFilesCount + groupMap.get("failedFilesCount");
							}
							groupMap.put("failedFilesCount", failedFilesCount);
						}

						// Processed files count
						processedFilesCount = groupCount;
						if (groupMap.containsKey("processedFilesCount")) {
							processedFilesCount = processedFilesCount + groupMap.get("processedFilesCount");
						}
						groupMap.put("processedFilesCount", processedFilesCount);

						resultMap.put(g_date, groupMap);
					}
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Map<String, Double>> getFMPassTrendSummary(List<Long> validationList, String startDate,
			String endDate) {
		Map<String, Map<String, Double>> resultMap = new LinkedHashMap<String, Map<String, Double>>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Select 10 distinct dates in descending order
			String dateSql = "select distinct date from file_tracking_history where idApp in (" + idAppListStr
					+ ") and date<='" + endDate + "' order by date desc limit 10";
			List<Date> dateList = jdbcTemplate.queryForList(dateSql, Date.class);
			String dateListStr = "";
			for (Date dt : dateList) {
				String f_date = sdf.format(dt);
				dateListStr = dateListStr + "'" + f_date + "',";
			}
			dateListStr = dateListStr.substring(0, dateListStr.length() - 1);

			// Get the processed,pass and fail files count for each date
			String statusCountSql = "select m1.sourceType, m1.date, (m1.passCount/m2.totalCount)*100 as passPercentage from (select t2.sourceType, t1.date, t1.status,count(*) as passCount from file_tracking_history t1 join locationMapping t2 on t1.idApp=t2.idApp where t1.idApp in ("
					+ idAppListStr + ") and t1.date in (" + dateListStr
					+ ") and t1.status='passed' group by t1.date,t1.status,t2.sourceType) m1 join ("
					+ "select s2.sourceType, s1.date,count(*) as totalCount from file_tracking_history s1 join locationMapping s2 on s1.idApp=s2.idApp where s1.idApp in ("
					+ idAppListStr + ") and s1.date in (" + dateListStr
					+ ") group by s1.date,s2.sourceType) m2 on m1.date=m2.date and m1.sourceType=m2.sourceType";

			LOG.debug("statusCountSql: " + statusCountSql);

			List<Map<String, Object>> statusList = jdbcTemplate.queryForList(statusCountSql);
			Set<String> sourceTypeList = new HashSet<String>();

			if (statusList != null && statusList.size() > 0) {

				for (Map<String, Object> statusMap : statusList) {

					if (statusMap != null && statusMap.size() > 0) {

						Date groupDate = (Date) statusMap.get("date");
						String g_date = sdf.format(groupDate);

						String sourceType = (String) statusMap.get("sourceType");

						// Store the unique sourceType
						sourceTypeList.add(sourceType);

						BigDecimal passPerc = (BigDecimal) statusMap.get("passPercentage");
						double passPercentage = 0.0;
						if (passPerc != null) {
							passPercentage = passPerc.doubleValue();
						}

						// Check if the group of date already exists
						Map<String, Double> groupMap = null;

						if (resultMap.containsKey(g_date)) {
							groupMap = resultMap.get(g_date);
						} else {
							groupMap = new HashMap<String, Double>();
						}

						groupMap.put(sourceType, passPercentage);

						resultMap.put(g_date, groupMap);
					}
				}

				for (String r_date : resultMap.keySet()) {
					Map<String, Double> s_Map = resultMap.get(r_date);

					for (String sourceType : sourceTypeList) {
						if (!s_Map.containsKey(sourceType)) {
							s_Map.put(sourceType, 0.0);
						}
					}

					resultMap.put(r_date, s_Map);
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Map<String, Double>> getABCCheckPassTrendSummary(List<Long> validationList, String startDate,
			String endDate) {
		Map<String, Map<String, Double>> resultMap = new LinkedHashMap<String, Map<String, Double>>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the sourceType of each idApp
			String fileNameSql = "select idApp, sourceType from locationMapping where idApp in (" + idAppListStr + ")";
			List<Map<String, Object>> idAppFileNameList = jdbcTemplate.queryForList(fileNameSql);
			Map<Long, String> app_sourceType_map = new HashMap<Long, String>();

			if (idAppFileNameList != null && idAppFileNameList.size() > 0) {
				for (Map<String, Object> s_map : idAppFileNameList) {
					if (s_map != null) {
						Integer appId = (Integer) s_map.get("idApp");
						Long l_appId = Long.parseLong(appId.toString());
						String fileName = (String) s_map.get("sourceType");
						app_sourceType_map.put(l_appId, fileName);
					}
				}

				// Get the processed,pass and fail files count for each date
				String statusCountSql = "select t3.Date,t3.AppId,Avg(t3.DQI) as DQI from (select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and Date<='" + endDate
						+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr + ") and t1.Date<='" + endDate + "') t3 group by t3.Date,t3.Run,t3.AppId";

				LOG.debug("statusCountSql: " + statusCountSql);

				List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

				Map<String, Map<String, Long>> ds_totalCount_map = new HashMap<String, Map<String, Long>>();
				Map<String, Map<String, Long>> ds_passedCount_map = new HashMap<String, Map<String, Long>>();
				Set<String> sourceTypeList = new HashSet<String>();

				if (statusList != null && statusList.size() > 0) {
					for (Map<String, Object> statusMap : statusList) {
						if (statusMap != null && statusMap.size() > 0) {
							String g_date = (String) statusMap.get("date");

							Integer l_idApp = (Integer) statusMap.get("AppId");
							Long idApp = Long.parseLong(l_idApp.toString());

							String sourceType = app_sourceType_map.get(idApp);

							// Store the unique sourceType
							sourceTypeList.add(sourceType);

							Double dqi = (Double) statusMap.get("DQI");
							long totalCount = 1l;
							long passCount = 0l;
							if (dqi == null) {
								dqi = 0.0;
							}

							if (dqi == 100) {
								passCount = 1l;
							}
							// Check if the group of date already exists
							Map<String, Long> tc_groupMap = null;
							Map<String, Long> pc_groupMap = null;

							if (ds_totalCount_map.containsKey(g_date)) {
								tc_groupMap = ds_totalCount_map.get(g_date);
							} else {
								tc_groupMap = new HashMap<String, Long>();
							}

							if (ds_passedCount_map.containsKey(g_date)) {
								pc_groupMap = ds_passedCount_map.get(g_date);
							} else {
								pc_groupMap = new HashMap<String, Long>();
							}

							if (tc_groupMap.containsKey(sourceType)) {
								totalCount = totalCount + tc_groupMap.get(sourceType);
							}
							tc_groupMap.put(sourceType, totalCount);

							if (pc_groupMap.containsKey(sourceType)) {
								passCount = passCount + pc_groupMap.get(sourceType);
							}
							pc_groupMap.put(sourceType, passCount);

							ds_totalCount_map.put(g_date, tc_groupMap);
							ds_passedCount_map.put(g_date, pc_groupMap);
						}
					}

					for (String date : ds_totalCount_map.keySet()) {
						Map<String, Long> source_totalcountMap = ds_totalCount_map.get(date);
						Map<String, Long> source_passcountMap = ds_passedCount_map.get(date);

						Map<String, Double> s_groupMap = null;
						if (resultMap.containsKey(date)) {
							s_groupMap = resultMap.get(date);
						} else {
							s_groupMap = new HashMap<String, Double>();
						}

						for (String g_sourceType : source_totalcountMap.keySet()) {

							Double passPercentage = 0.0;
							Long totalCount = source_totalcountMap.get(g_sourceType);
							Long passCount = source_passcountMap.get(g_sourceType);

							if (totalCount == null) {
								totalCount = 0l;
							}

							if (passCount == null) {
								passCount = 0l;
							}
							passPercentage = ((double) passCount / totalCount) * 100;
							if (passPercentage == null || passPercentage.isInfinite() || passPercentage.isNaN()) {
								passPercentage = 0.0;
							}
							s_groupMap.put(g_sourceType, passPercentage);

						}

						resultMap.put(date, s_groupMap);

					}

					for (String r_date : resultMap.keySet()) {
						Map<String, Double> s_Map = resultMap.get(r_date);

						for (String sourceType : sourceTypeList) {
							if (!s_Map.containsKey(sourceType)) {
								s_Map.put(sourceType, 0.0);
							}
						}

						resultMap.put(r_date, s_Map);
					}
				}

			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public Map<String, Map<String, Double>> getTrendCheckPassTrendSummary(List<Long> validationList, String startDate,
			String endDate) {
		Map<String, Map<String, Double>> resultMap = new LinkedHashMap<String, Map<String, Double>>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the sourceType of each idApp
			String fileNameSql = "select idApp, sourceType from locationMapping where idApp in (" + idAppListStr + ")";
			List<Map<String, Object>> idAppFileNameList = jdbcTemplate.queryForList(fileNameSql);
			Map<Long, String> app_sourceType_map = new HashMap<Long, String>();

			if (idAppFileNameList != null && idAppFileNameList.size() > 0) {
				for (Map<String, Object> s_map : idAppFileNameList) {
					if (s_map != null) {
						Integer appId = (Integer) s_map.get("idApp");
						Long l_appId = Long.parseLong(appId.toString());
						String fileName = (String) s_map.get("sourceType");
						app_sourceType_map.put(l_appId, fileName);
					}
				}

				// Get the processed,pass and fail files count for each date
				String statusCountSql = "select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and Date<='" + endDate
						+ "' and Test='DQ_Numerical Field Fingerprint' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
						+ idAppListStr + ") and t1.Date<='" + endDate
						+ "' and t1.Test='DQ_Numerical Field Fingerprint'";

				LOG.debug("statusCountSql: " + statusCountSql);

				List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

				Map<String, Map<String, Long>> ds_totalCount_map = new HashMap<String, Map<String, Long>>();
				Map<String, Map<String, Long>> ds_passedCount_map = new HashMap<String, Map<String, Long>>();
				Set<String> sourceTypeList = new HashSet<String>();

				if (statusList != null && statusList.size() > 0) {
					for (Map<String, Object> statusMap : statusList) {
						if (statusMap != null && statusMap.size() > 0) {
							String g_date = (String) statusMap.get("date");

							Integer l_idApp = (Integer) statusMap.get("AppId");
							Long idApp = Long.parseLong(l_idApp.toString());

							String sourceType = app_sourceType_map.get(idApp);
							// Store the unique sourceType
							sourceTypeList.add(sourceType);

							Double dqi = (Double) statusMap.get("DQI");
							long totalCount = 1l;
							long passCount = 0l;
							if (dqi == null) {
								dqi = 0.0;
							}

							if (dqi == 100) {
								passCount = 1l;
							}
							// Check if the group of date already exists
							Map<String, Long> tc_groupMap = null;
							Map<String, Long> pc_groupMap = null;

							if (ds_totalCount_map.containsKey(g_date)) {
								tc_groupMap = ds_totalCount_map.get(g_date);
							} else {
								tc_groupMap = new HashMap<String, Long>();
							}

							if (ds_passedCount_map.containsKey(g_date)) {
								pc_groupMap = ds_passedCount_map.get(g_date);
							} else {
								pc_groupMap = new HashMap<String, Long>();
							}

							if (tc_groupMap.containsKey(sourceType)) {
								totalCount = totalCount + tc_groupMap.get(sourceType);
							}
							tc_groupMap.put(sourceType, totalCount);

							if (pc_groupMap.containsKey(sourceType)) {
								passCount = passCount + pc_groupMap.get(sourceType);
							}
							pc_groupMap.put(sourceType, passCount);

							ds_totalCount_map.put(g_date, tc_groupMap);
							ds_passedCount_map.put(g_date, pc_groupMap);
						}
					}

					for (String date : ds_totalCount_map.keySet()) {
						Map<String, Long> source_totalcountMap = ds_totalCount_map.get(date);
						Map<String, Long> source_passcountMap = ds_passedCount_map.get(date);

						Map<String, Double> s_groupMap = null;
						if (resultMap.containsKey(date)) {
							s_groupMap = resultMap.get(date);
						} else {
							s_groupMap = new HashMap<String, Double>();
						}

						for (String g_sourceType : source_totalcountMap.keySet()) {

							Double passPercentage = 0.0;
							Long totalCount = source_totalcountMap.get(g_sourceType);
							Long passCount = source_passcountMap.get(g_sourceType);

							if (totalCount == null) {
								totalCount = 0l;
							}

							if (passCount == null) {
								passCount = 0l;
							}
							passPercentage = ((double) passCount / totalCount) * 100;
							if (passPercentage == null || passPercentage.isInfinite() || passPercentage.isNaN()) {
								passPercentage = 0.0;
							}
							s_groupMap.put(g_sourceType, passPercentage);

						}

						resultMap.put(date, s_groupMap);

					}

					for (String r_date : resultMap.keySet()) {
						Map<String, Double> s_Map = resultMap.get(r_date);

						for (String sourceType : sourceTypeList) {
							if (!s_Map.containsKey(sourceType)) {
								s_Map.put(sourceType, 0.0);
							}
						}

						resultMap.put(r_date, s_Map);
					}
				}

			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return resultMap;
	}

	@Override
	public List<Long> getValdiationsForSourceLocation(String sourceType, Integer locationId) {
		List<Long> validationList = new ArrayList<Long>();
		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and locationId=?";
			validationList = jdbcTemplate.queryForList(sql, Long.class, sourceType, locationId);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return validationList;
	}

	@Override
	public List<String> getFileNameListForSource(String sourceType, String source) {
		List<String> fileNameList = new ArrayList<String>();
		try {
			String sql = "select distinct fileName from locationMapping where sourceType=? and source=?";
			fileNameList = jdbcTemplate.queryForList(sql, String.class, sourceType, source);
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return fileNameList;
	}

	@Override
	public List<FMFilePerformanceSummary> getFMDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate) {

		List<FMFilePerformanceSummary> finalSummaryList = new ArrayList<FMFilePerformanceSummary>();

		try {
			// Select 10 distinct dates in descending order
			String dateSql = "select distinct date from file_tracking_history t1 join locationMapping t2 on t1.idApp=t2.idApp and t1.date<=? and t2.sourceType=? and t2.source=? and t2.fileName=? order by t1.date desc limit 10";
			List<Date> dateList = jdbcTemplate.queryForList(dateSql, Date.class, endDate, sourceType, source, fileName);

			for (Date dt : dateList) {
				String f_date = sdf.format(dt);

				String sql = "select t1.* from file_tracking_history t1 join locationMapping t2 on t1.idApp=t2.idApp and t1.date=? and t2.sourceType=? and t2.source=? and t2.fileName=? order by t1.id desc limit 1";

				List<FMFilePerformanceSummary> dateSummary = jdbcTemplate.query(sql,
						new RowMapper<FMFilePerformanceSummary>() {

							@Override
							public FMFilePerformanceSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
								FMFilePerformanceSummary fileCheckReport = new FMFilePerformanceSummary();
								int totalChecks = 0;
								int totalChecksPassed = 0;
								String fileFormat = rs.getString("fileFormat");
								String overallStatus = rs.getString("status");
								String zeroSizeFileCheck = rs.getString("zeroSizeFileCheck");
								String columnCountCheck = rs.getString("columnCountCheck");
								String columnSequenceCheck = rs.getString("columnSequenceCheck");
								String recordLengthCheck = rs.getString("recordLengthCheck");
								String recordMaxLengthCheck = rs.getString("recordMaxLengthCheck");
								
								if (fileFormat != null && fileFormat.equalsIgnoreCase("FLAT")) {

									totalChecks = totalChecks + 4;

									if (recordLengthCheck != null && recordLengthCheck.equalsIgnoreCase("passed")) {
										++totalChecksPassed;
									}
									if (recordMaxLengthCheck != null && recordMaxLengthCheck.equalsIgnoreCase("passed")) {
										++totalChecksPassed;
									}
								} else {
									totalChecks = totalChecks + 3;
								}

								if (zeroSizeFileCheck != null && zeroSizeFileCheck.equalsIgnoreCase("passed")) {
									++totalChecksPassed;
								}

								if (columnCountCheck != null && columnCountCheck.equalsIgnoreCase("passed")) {
									++totalChecksPassed;
								}

								if (columnSequenceCheck != null && columnSequenceCheck.equalsIgnoreCase("passed")) {
									++totalChecksPassed;
								}

								double overallScore = ((double) totalChecksPassed / totalChecks) * 100;
								fileCheckReport.setDate(f_date);
								fileCheckReport.setOverallScore(overallScore);
								fileCheckReport.setOverallStatus(overallStatus);
								fileCheckReport.setZeroSizeFileCheck(zeroSizeFileCheck);
								fileCheckReport.setColumnCountCheck(columnCountCheck);
								fileCheckReport.setColumnSequenceCheck(columnSequenceCheck);
								fileCheckReport.setRecordLengthCheck(recordLengthCheck);
								fileCheckReport.setRecordMaxLengthCheck(recordMaxLengthCheck);
								
								return fileCheckReport;
							}

						}, f_date, sourceType, source, fileName);

				finalSummaryList.addAll(dateSummary);
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return finalSummaryList;

	}

	@Override
	public List<ABCCheckFailedReport> getABCCheckFailedReportForDateRange(String startDate, String endDate,
			List<Long> validationList) {
		List<ABCCheckFailedReport> resultList = null;

		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the fileName for validations
			String fileNameSql = "select idApp, fileName from locationMapping where idApp in (" + idAppListStr + ")";
			List<Map<String, Object>> idAppFileNameList = jdbcTemplate.queryForList(fileNameSql);
			Map<Long, String> app_file_map = new HashMap<Long, String>();

			if (idAppFileNameList != null && idAppFileNameList.size() > 0) {
				for (Map<String, Object> f_map : idAppFileNameList) {
					if (f_map != null) {
						Integer appId = (Integer) f_map.get("idApp");
						Long l_appId = Long.parseLong(appId.toString());
						String fileName = (String) f_map.get("fileName");
						app_file_map.put(l_appId, fileName);
					}
				}

				Map<Long, ABCCheckFailedReport> app_check_map = new HashMap<Long, ABCCheckFailedReport>();

				// Get the distinct date and max Run of date for each validation
				String sql = "select m2.AppId, m2.date, m2.Run,m1.DQI, m1.Test from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 join (select AppId,date,max(Run) as run from DashBoard_Summary where AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run";
				LOG.debug("Sql: " + sql);

				List<Map<String, Object>> appCheckList = jdbcTemplate1.queryForList(sql);

				if (appCheckList != null && appCheckList.size() > 0) {
					for (Map<String, Object> statusMap : appCheckList) {

						if (statusMap != null && statusMap.size() > 0) {
							Integer l_idApp = (Integer) statusMap.get("AppId");
							Long appID = Long.parseLong(l_idApp.toString());
							if (appID != null) {
								ABCCheckFailedReport abcCheckFailedReport = null;
								if (app_check_map.containsKey(appID)) {
									abcCheckFailedReport = app_check_map.get(appID);
								} else {
									abcCheckFailedReport = new ABCCheckFailedReport();
									abcCheckFailedReport.setFileName(app_file_map.get(appID));
									abcCheckFailedReport.setOverallStatus("failed");
								}
								Double dqi = (Double) statusMap.get("DQI");
								String status = "";
								if (dqi != null) {
									if (dqi == 100) {
										status = "passed";
									} else {
										status = "failed";
									}
								}

								String test = (String) statusMap.get("Test");
								if (test.equalsIgnoreCase("DQ_Record Count Fingerprint")) {
									abcCheckFailedReport.setRecordReasonabilityStatus(status);
								} else if (test.equalsIgnoreCase("DQ_Completeness")) {
									abcCheckFailedReport.setNullCheckStatus(status);
								} else if (test.equalsIgnoreCase("DQ_LengthCheck")) {
									abcCheckFailedReport.setLengthCheckStatus(status);
								}else if (test.equalsIgnoreCase("DQ_MaxLengthCheck")) {
									abcCheckFailedReport.setMaxLengthCheckStatus(status);
								} else if (test.equalsIgnoreCase("DQ_Rules")) {
									abcCheckFailedReport.setDomainCheckStatus(status);
								}

								app_check_map.put(appID, abcCheckFailedReport);
							}
						}
					}

					resultList = new ArrayList<ABCCheckFailedReport>();

					for (long appId : app_check_map.keySet()) {
						ABCCheckFailedReport abcCheckFailedReport = app_check_map.get(appId);
						String rc_status = abcCheckFailedReport.getRecordReasonabilityStatus();
						String ln_status = abcCheckFailedReport.getLengthCheckStatus();
						String maxln_status = abcCheckFailedReport.getMaxLengthCheckStatus();
						
						String null_status = abcCheckFailedReport.getNullCheckStatus();
						String dm_status = abcCheckFailedReport.getDomainCheckStatus();

						if ((rc_status != null && rc_status.equalsIgnoreCase("failed"))
								|| (ln_status != null && ln_status.equalsIgnoreCase("failed"))
								|| (maxln_status != null && maxln_status.equalsIgnoreCase("failed"))
								|| (null_status != null && null_status.equalsIgnoreCase("failed"))
								|| (dm_status != null && dm_status.equalsIgnoreCase("failed"))) {
							resultList.add(abcCheckFailedReport);
						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<TrendCheckFailedReport> getTrendCheckFailedReportForDateRange(String startDate, String endDate,
			List<Long> validationList) {
		List<TrendCheckFailedReport> resultList = null;

		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the fileName for validations
			String fileNameSql = "select idApp, fileName from locationMapping where idApp in (" + idAppListStr + ")";
			List<Map<String, Object>> idAppFileNameList = jdbcTemplate.queryForList(fileNameSql);
			Map<Long, String> app_file_map = new HashMap<Long, String>();

			if (idAppFileNameList != null && idAppFileNameList.size() > 0) {
				for (Map<String, Object> f_map : idAppFileNameList) {
					if (f_map != null) {
						Integer appId = (Integer) f_map.get("idApp");
						Long l_appId = Long.parseLong(appId.toString());
						String fileName = (String) f_map.get("fileName");
						app_file_map.put(l_appId, fileName);
					}
				}

				// Get the distinct date and max Run of date for each validation
				String sql = "select m2.AppId, m2.date, m2.Run,m1.DQI, m1.Key_Metric_1,m1.Key_Metric_2,m1.Key_Metric_3 from DashBoard_Summary m1 join (select t1.AppId, t1.date, t2.Run from (select AppId, max(date) as date from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId) t1 "
						+ "join (select AppId,date,max(Run) as run from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
						+ idAppListStr + ") and date>='" + startDate + "' and date<='" + endDate
						+ "' group by AppId,date) t2 on t1.AppId=t2.AppId and t1.date=t2.date) m2 on m1.AppId=m2.AppId and m1.date=m2.date and m1.Run=m2.Run and m1.Test='DQ_Numerical Field Fingerprint' and m1.DQI<100.0 and m1.DQI is not null";
				LOG.debug("Sql: " + sql);

				resultList = jdbcTemplate1.query(sql, new RowMapper<TrendCheckFailedReport>() {
					@Override
					public TrendCheckFailedReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckFailedReport trendCheckFailedReport = new TrendCheckFailedReport();
						long appID = rs.getLong("AppId");
						int noOfTrendChecks = rs.getInt("Key_Metric_1");
						int noOfChecksFailed = rs.getInt("Key_Metric_2");
						String columnsInvolved = rs.getString("Key_Metric_3");
						String overallStatus = "failed";
						trendCheckFailedReport.setFileName(app_file_map.get(appID));
						trendCheckFailedReport.setOverallStatus(overallStatus);
						trendCheckFailedReport.setNoOfTrendChecks(noOfTrendChecks);
						trendCheckFailedReport.setNoOfChecksFailed(noOfChecksFailed);
						trendCheckFailedReport.setColumnsInvolved(columnsInvolved);
						return trendCheckFailedReport;
					}
				});
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<TrendCheckSummaryReport> getTrendCheckSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId) {

		List<TrendCheckSummaryReport> resultList = new ArrayList<TrendCheckSummaryReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);

			if (idApp != null) {
				String tableName = "DATA_QUALITY_Column_Summary";
				String numSumStatus = ", CASE WHEN (ABS(sumOfNumStat-NumSumAvg)/NumSumStdDev) > NumSumThreshold THEN 'failed' ELSE 'passed' END AS NumSumStatus";

				String dataSql = "select * " + numSumStatus + " from " + tableName + " where idApp=" + idApp
						+ " and Date=? and Run = (select max(Run) from " + tableName + " where idApp=" + idApp
						+ " and Date=?) order by NumMeanStatus,NumSDStatus,NumSumStatus";
				LOG.debug("dataSql: " + dataSql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckSummaryReport>() {
					@Override
					public TrendCheckSummaryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckSummaryReport trendCheckSummaryReport = new TrendCheckSummaryReport();
						trendCheckSummaryReport.setMicrosegment(rs.getString("dGroupCol"));
						trendCheckSummaryReport.setMicrosegmentValue(rs.getString("dGroupVal"));
						trendCheckSummaryReport.setColumnName(rs.getString("ColName"));
						trendCheckSummaryReport.setCount(rs.getLong("Count"));
						trendCheckSummaryReport.setMinValue(rs.getDouble("Min"));
						trendCheckSummaryReport.setMaxValue(rs.getDouble("Max"));
						trendCheckSummaryReport.setMeanValue(rs.getDouble("Mean"));
						trendCheckSummaryReport.setStd_dev(rs.getDouble("Std_Dev"));
						String numMeanStatus = rs.getString("NumMeanStatus");
						String numSDStatus = rs.getString("NumSDStatus");
						String numSumStatus = rs.getString("NumSumStatus");
						String status = "";
						if (numMeanStatus != null && numMeanStatus.equalsIgnoreCase("passed") && numSDStatus != null
								&& numSDStatus.equalsIgnoreCase("passed") && numSumStatus != null
								&& numSumStatus.equalsIgnoreCase("passed")) {
							status = "passed";
						} else if ((numMeanStatus != null && numMeanStatus.equalsIgnoreCase("failed"))
								|| (numSDStatus != null && numSDStatus.equalsIgnoreCase("failed"))
								|| (numSumStatus != null && numSumStatus.equalsIgnoreCase("failed"))) {
							status = "failed";
						}
						trendCheckSummaryReport.setStatus(status);
						return trendCheckSummaryReport;
					}
				}, processedDate, processedDate);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<TrendCheckPerformanceSummary> getTrendCheckDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate, Integer locationId) {

		List<TrendCheckPerformanceSummary> resultList = new ArrayList<TrendCheckPerformanceSummary>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);

			if (idApp != null) {
				// Get the distinct date and max Run of date for each validation
				String dataSql = "select t1.Date,t1.Run,t1.DQI,t1.Key_Metric_1,t1.Key_Metric_2,(select Sum(RecordCount) from DATA_QUALITY_Transactionset_sum_A1 where Date=t1.Date and Run=t1.Run and t1.idApp="
						+ idApp
						+ ") as RecordCount from DashBoard_Summary t1 join (select Date,max(Run) as Run from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId="
						+ idApp + " and Date<='" + endDate
						+ "' group by Date) t2 on t1.Date=t2.Date and t1.Run=t2.Run and t1.Test='DQ_Numerical Field Fingerprint' and t1.AppId="
						+ idApp + " and t1.Date<='" + endDate + "' order by t1.Date desc limit 10";

				LOG.debug("dataSql: " + sql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckPerformanceSummary>() {
					@Override
					public TrendCheckPerformanceSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckPerformanceSummary trendCheckPerfSmry = new TrendCheckPerformanceSummary();
						Date trend_date = rs.getDate("Date");
						String t_Date = sdf.format(trend_date);

						double dqi = rs.getDouble("DQI");
						int noOfTrendChecks = rs.getInt("Key_Metric_1");
						int noOfChecksFailed = rs.getInt("Key_Metric_2");
						long recordCount = rs.getLong("RecordCount");
						if (noOfChecksFailed == 0 && dqi == 0.0) {
							dqi = 100.0;
						}
						trendCheckPerfSmry.setDate(t_Date);
						trendCheckPerfSmry.setOverallScore(dqi);
						trendCheckPerfSmry.setNoOfTrendChecks(noOfTrendChecks);
						trendCheckPerfSmry.setNoOfFailedChecks(noOfChecksFailed);
						trendCheckPerfSmry.setRecordCount(recordCount);
						return trendCheckPerfSmry;
					}
				});
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<AVERPerformanceSummary> getABCCheckPerformanceSummary(List<Long> validationList, String startDate,
			String endDate) {
		List<AVERPerformanceSummary> summryList = new ArrayList<AVERPerformanceSummary>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the processed,pass and fail files count for each date
			String statusCountSql = "select t4.Date,sum(t4.DQI=100) as passedfiles, sum(t4.DQI<100) as failedfiles, count(*) as processedFiles  from (select t3.Date,t3.Run,t3.AppId,Avg(t3.DQI) as DQI from (select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where AppId in ("
					+ idAppListStr + ") and Date<='" + endDate
					+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId in ("
					+ idAppListStr + ") and t1.Date<='" + endDate
					+ "') t3 group by t3.Date,t3.Run,t3.AppId) t4 group by t4.Date order by t4.Date desc limit 10";
			LOG.debug("statusCountSql: " + statusCountSql);

			List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

			if (statusList != null && statusList.size() > 0) {
				for (Map<String, Object> statusMap : statusList) {
					if (statusMap != null && statusMap.size() > 0) {

						String g_date = (String) statusMap.get("date");

						BigDecimal p_count = (BigDecimal) statusMap.get("passedfiles");
						long passedFilesCount = 0l;
						if (p_count != null) {
							passedFilesCount = p_count.longValue();
						}

						BigDecimal f_count = (BigDecimal) statusMap.get("failedfiles");
						long failedFilesCount = 0l;
						if (f_count != null) {
							failedFilesCount = f_count.longValue();
						}

						Long processedFilesCount = (Long) statusMap.get("processedFiles");
						if (processedFilesCount == null) {
							processedFilesCount = 0l;
						}

						Double failPercentage = ((double) failedFilesCount / processedFilesCount) * 100;
						if (failPercentage == null || failPercentage.isInfinite() || failPercentage.isNaN()) {
							failPercentage = 0.0;
						}

						AVERPerformanceSummary summry = new AVERPerformanceSummary();
						summry.setDate(g_date);
						summry.setProcessedFilesCount(processedFilesCount);
						summry.setFailedFilesCount(failedFilesCount);
						summry.setPassedFilesCount(passedFilesCount);
						summry.setFailPercentage(failPercentage);
						summryList.add(summry);

					}
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return summryList;
	}

	@Override
	public List<AVERPerformanceSummary> getTrendCheckPerformanceSummary(List<Long> validationList, String startDate,
			String endDate) {
		List<AVERPerformanceSummary> summryList = new ArrayList<AVERPerformanceSummary>();
		try {
			String idAppListStr = "";
			for (Long idApp : validationList) {
				idAppListStr = idAppListStr + idApp + ",";
			}
			idAppListStr = idAppListStr.substring(0, idAppListStr.length() - 1);

			// Get the processed,pass and fail files count for each date
			String statusCountSql = "select t3.Date,sum(t3.DQI=100) as passedfiles, sum(t3.DQI<100) as failedfiles, count(*) as processedFiles  from (select t1.Date,t1.Run,t1.AppId,case when (t1.DQI IS NULL) then 0 else t1.DQI end as DQI from DashBoard_Summary t1 join (select AppId,Date,max(Run) as Run from DashBoard_Summary where Test='DQ_Numerical Field Fingerprint' and AppId in ("
					+ idAppListStr + ") and Date<='" + endDate
					+ "' group by Date,AppId) t2 on t1.AppId=t2.AppId and t1.Date=t2.Date and t1.Run=t2.Run and t1.Test='DQ_Numerical Field Fingerprint' and t1.AppId in ("
					+ idAppListStr + ") and t1.Date<='" + endDate
					+ "') t3 group by t3.Date order by t3.Date desc limit 10";

			LOG.debug("statusCountSql: " + statusCountSql);

			List<Map<String, Object>> statusList = jdbcTemplate1.queryForList(statusCountSql);

			if (statusList != null && statusList.size() > 0) {
				for (Map<String, Object> statusMap : statusList) {
					if (statusMap != null && statusMap.size() > 0) {

						String g_date = (String) statusMap.get("date");

						BigDecimal p_count = (BigDecimal) statusMap.get("passedfiles");
						long passedFilesCount = 0l;
						if (p_count != null) {
							passedFilesCount = p_count.longValue();
						}

						BigDecimal f_count = (BigDecimal) statusMap.get("failedfiles");
						long failedFilesCount = 0l;
						if (f_count != null) {
							failedFilesCount = f_count.longValue();
						}

						Long processedFilesCount = (Long) statusMap.get("processedFiles");
						if (processedFilesCount == null) {
							processedFilesCount = 0l;
						}

						Double failPercentage = ((double) failedFilesCount / processedFilesCount) * 100;
						if (failPercentage == null || failPercentage.isInfinite() || failPercentage.isNaN()) {
							failPercentage = 0.0;
						}
						AVERPerformanceSummary summry = new AVERPerformanceSummary();
						summry.setDate(g_date);
						summry.setProcessedFilesCount(processedFilesCount);
						summry.setFailedFilesCount(failedFilesCount);
						summry.setPassedFilesCount(passedFilesCount);
						summry.setFailPercentage(failPercentage);
						summryList.add(summry);

					}
				}
			}

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}

		return summryList;
	}

	@Override
	public List<ABCCheckPerformanceSummary> getABCCheckDailyPerformanceForFile(String sourceType, String source,
			String fileName, String startDate, String endDate, Integer locationId) {

		List<ABCCheckPerformanceSummary> resultList = new ArrayList<ABCCheckPerformanceSummary>();

		try {
			Map<String, ABCCheckPerformanceSummary> date_check_map = new HashMap<String, ABCCheckPerformanceSummary>();

			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);

			if (idApp != null) {
				// Select 10 distinct dates in descending order
				String dateSql = "select distinct Date from DashBoard_Summary where AppId =" + idApp + " and Date<='"
						+ endDate + "' order by Date desc limit 10";
				List<Date> dateList = jdbcTemplate1.queryForList(dateSql, Date.class);
				if (dateList != null && dateList.size() > 0) {
					String dateListStr = "";
					for (Date dt : dateList) {
						String f_date = sdf.format(dt);
						dateListStr = dateListStr + "'" + f_date + "',";
					}
					dateListStr = dateListStr.substring(0, dateListStr.length() - 1);

					// Get the distinct date and max Run of date for each validation
					String dataSql = "select t1.Date,t1.Run,t1.DQI,t1.Test,(select Sum(RecordCount) from DATA_QUALITY_Transactionset_sum_A1 where Date=t1.Date and Run=t1.Run and idApp="
							+ idApp
							+ ") as RecordCount from DashBoard_Summary t1 join (select Date,max(Run) as Run from DashBoard_Summary where AppId="
							+ idApp + " and Date in (" + dateListStr
							+ ") group by Date) t2 on t1.Date=t2.Date and t1.Run=t2.Run and t1.AppId=" + idApp
							+ " and t1.Date in (" + dateListStr
							+ ") and t1.Test in ('DQ_Record Count Fingerprint','DQ_Completeness','DQ_LengthCheck','DQ_MaxLengthCheck','DQ_Rules') order by t1.Date desc";

					LOG.debug("dataSql: " + dataSql);

					List<Map<String, Object>> appCheckList = jdbcTemplate1.queryForList(dataSql);

					if (appCheckList != null && appCheckList.size() > 0) {
						for (Map<String, Object> statusMap : appCheckList) {

							if (statusMap != null && statusMap.size() > 0) {
								String g_date = (String) statusMap.get("Date");
								ABCCheckPerformanceSummary abcCheckPerformanceSummary = null;
								if (date_check_map.containsKey(g_date)) {
									abcCheckPerformanceSummary = date_check_map.get(g_date);
								} else {
									abcCheckPerformanceSummary = new ABCCheckPerformanceSummary();
								}
								abcCheckPerformanceSummary.setDate(g_date);

								// Get RecordCount
								BigDecimal bg_recordCount = (BigDecimal) statusMap.get("RecordCount");
								Long recordCount = 0l;
								if (bg_recordCount != null) {
									recordCount = bg_recordCount.longValue();
								}
								abcCheckPerformanceSummary.setRecordCount(recordCount);

								// Get DQI
								Double dqi = (Double) statusMap.get("DQI");
								String status = "";
								if (dqi != null) {
									if (dqi == 100) {
										status = "passed";
									} else {
										status = "failed";
									}
								}

								String test = (String) statusMap.get("Test");
								boolean testFound = false;

								if (test.equalsIgnoreCase("DQ_Record Count Fingerprint")) {
									testFound = true;
									abcCheckPerformanceSummary.setRecordReasonabilityStatus(status);
								} else if (test.equalsIgnoreCase("DQ_Completeness")) {
									testFound = true;
									abcCheckPerformanceSummary.setNullCheckStatus(status);
								} else if (test.equalsIgnoreCase("DQ_LengthCheck")) {
									testFound = true;
									abcCheckPerformanceSummary.setLengthCheckStatus(status);
								} else if (test.equalsIgnoreCase("DQ_MaxLengthCheck")) {
									testFound = true;
									abcCheckPerformanceSummary.setMaxLengthCheckStatus(status);
								} else if (test.equalsIgnoreCase("DQ_Rules")) {
									testFound = true;
									abcCheckPerformanceSummary.setDomainCheckStatus(status);
								}

								if (testFound) {
									if (dqi == null) {
										dqi = 0.0;
									}
									dqi = dqi + abcCheckPerformanceSummary.getOverallScore();
									abcCheckPerformanceSummary.setOverallScore(dqi);
								}

								date_check_map.put(g_date, abcCheckPerformanceSummary);
							}
						}

						resultList = new ArrayList<ABCCheckPerformanceSummary>();

						for (String date : date_check_map.keySet()) {
							ABCCheckPerformanceSummary abcCheckPerformanceSummary = date_check_map.get(date);
							String rc_status = abcCheckPerformanceSummary.getRecordReasonabilityStatus();
							String ln_status = abcCheckPerformanceSummary.getLengthCheckStatus();
							String maxln_status = abcCheckPerformanceSummary.getMaxLengthCheckStatus();
							String null_status = abcCheckPerformanceSummary.getNullCheckStatus();
							String dm_status = abcCheckPerformanceSummary.getDomainCheckStatus();

							double dqi = abcCheckPerformanceSummary.getOverallScore();
							long totalChecks = 0;

							if (rc_status != null && !rc_status.trim().isEmpty()) {
								++totalChecks;
							}
							if (ln_status != null && !ln_status.trim().isEmpty()) {
								++totalChecks;
							}
							if (maxln_status != null && !maxln_status.trim().isEmpty()) {
								++totalChecks;
							}
							if (null_status != null && !null_status.trim().isEmpty()) {
								++totalChecks;
							}
							if (dm_status != null && !dm_status.trim().isEmpty()) {
								++totalChecks;
							}

							Double overallScore = (dqi / totalChecks);
							if (overallScore == null || overallScore.isInfinite() || overallScore.isNaN()) {
								overallScore = 0.0;
							}

							abcCheckPerformanceSummary.setOverallScore(overallScore);
							resultList.add(abcCheckPerformanceSummary);
						}

					}
				}
			}
		} catch (

		Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;

	}

	@Override
	public List<ABCCheckSummaryReport> getABCCheckSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId) {

		List<ABCCheckSummaryReport> resultList = new ArrayList<ABCCheckSummaryReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);

			if (idApp != null) {
				String nullChecktableName = "DATA_QUALITY_NullCheck_Summary";
				String lengthChecktableName = "DATA_QUALITY_Length_Check";
				String rulesTableName = "DATA_QUALITY_" + idApp + "_RULES";
				String rcaTableName = "DATA_QUALITY_Transactionset_sum_A1";

				// Get maxDate and Run
				String dateRunSql = "select max(Run) from " + rcaTableName + " where Date='" + processedDate
						+ "' and idApp=?";
				Long maxRun = jdbcTemplate1.queryForObject(dateRunSql, Long.class, idApp);

				if (maxRun != null && maxRun != 0l) {
					String nullDataSql = "select 'NullCheck' as Rule,colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, Status from "
							+ nullChecktableName + " where idApp=" + idApp + "  and Date='" + processedDate
							+ "' and Run = " + maxRun;

					String lengthDataSql = "select 'LengthCheck' as Rule, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, Length_Threshold as Threshold, Status from "
							+ lengthChecktableName + " where idApp = " + idApp + " and  Date='" + processedDate
							+ "' and Run = " + maxRun;

					String rulesDataSql = "select ruleName as Rule, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status from "
							+ rulesTableName + " where Date='" + processedDate + "' and Run = " + maxRun;

					String dataSql = "(" + nullDataSql + ") union (" + lengthDataSql + ") union (" + rulesDataSql + ")";
					LOG.debug("dataSql: " + dataSql);

					resultList = jdbcTemplate1.query(dataSql, new RowMapper<ABCCheckSummaryReport>() {
						@Override
						public ABCCheckSummaryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
							ABCCheckSummaryReport abcCheckSummaryReport = new ABCCheckSummaryReport();
							abcCheckSummaryReport.setRule(rs.getString("Rule"));
							abcCheckSummaryReport.setColumnName(rs.getString("ColumnName"));
							abcCheckSummaryReport.setRecordCount(rs.getLong("RecordCount"));
							abcCheckSummaryReport.setRecordFailed(rs.getLong("RecordFailed"));
							abcCheckSummaryReport.setFailPercentage(rs.getDouble("FailedPerc"));
							abcCheckSummaryReport.setThreshold(rs.getDouble("Threshold"));
							abcCheckSummaryReport.setStatus(rs.getString("Status"));
							return abcCheckSummaryReport;
						}
					});
				}
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;

	}

	@Override
	public List<ABCCheckRuleSummaryReport> getABCCheckRuleSummaryDetailsOfFile(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String ruleName, String columnName) {

		List<ABCCheckRuleSummaryReport> resultList = new ArrayList<ABCCheckRuleSummaryReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);

			if (idApp != null) {
				String nullChecktableName = "DATA_QUALITY_NullCheck_Summary";
				String lengthChecktableName = "DATA_QUALITY_Length_Check";
				String rulesTableName = "DATA_QUALITY_" + idApp + "_RULES";

				if (ruleName != null) {

					String nullDataSql = "select Date, Run, 'NullCheck' as Rule,colName as ColumnName, Record_Count as RecordCount, Null_Value as RecordFailed, Null_Percentage as FailedPerc, Null_Threshold as  Threshold, Status from "
							+ nullChecktableName + " where idApp=" + idApp + " and Date='" + processedDate
							+ "' and colName='" + columnName + "'";

					String lengthDataSql = "select Date, Run, 'LengthCheck' as Rule, ColName as ColumnName, RecordCount, TotalFailedRecords as RecordFailed, FailedRecords_Percentage as FailedPerc, Length_Threshold as Threshold, Status from "
							+ lengthChecktableName + " where idApp = " + idApp + " and Date='" + processedDate
							+ "' and colName='" + columnName + "'";

					String rulesDataSql = "select Date, Run, ruleName as Rule, '' as ColumnName, totalRecords as RecordCount, totalFailed as  RecordFailed, rulePercentage as FailedPerc, ruleThreshold as Threshold, Status from "
							+ rulesTableName + " where Date='" + processedDate + "' and ruleName='" + ruleName + "'";

					String dataSql = "";
					if (ruleName.trim().equalsIgnoreCase("NullCheck")) {
						dataSql = nullDataSql;
					} else if (ruleName.trim().equalsIgnoreCase("LengthCheck")) {
						dataSql = lengthDataSql;
					} else {
						dataSql = rulesDataSql;
					}
					LOG.debug("dataSql: " + dataSql);

					resultList = jdbcTemplate1.query(dataSql, new RowMapper<ABCCheckRuleSummaryReport>() {
						@Override
						public ABCCheckRuleSummaryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
							ABCCheckRuleSummaryReport abcCheckRuleSummaryReport = new ABCCheckRuleSummaryReport();
							abcCheckRuleSummaryReport.setDate(rs.getString("Date"));
							abcCheckRuleSummaryReport.setRun(rs.getLong("Run"));
							abcCheckRuleSummaryReport.setRule(rs.getString("Rule"));
							abcCheckRuleSummaryReport.setRecordCount(rs.getLong("RecordCount"));
							abcCheckRuleSummaryReport.setRecordFailed(rs.getLong("RecordFailed"));
							abcCheckRuleSummaryReport.setFailPercentage(rs.getDouble("FailedPerc"));
							abcCheckRuleSummaryReport.setThreshold(rs.getDouble("Threshold"));
							abcCheckRuleSummaryReport.setStatus(rs.getString("Status"));
							return abcCheckRuleSummaryReport;
						}
					});
				}
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	// TODO : delete this
	@Override
	public List<TrendCheckMicrosegmentReport> getTrendCheckMicrosegmentSummary(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName) {

		List<TrendCheckMicrosegmentReport> resultList = new ArrayList<TrendCheckMicrosegmentReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = null;
			try {
				idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);
			} catch (Exception e) {
				LOG.error("Failed to get the idApp !!");
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			if (idApp != null) {
				String tableName = "DATA_QUALITY_Column_Summary";

				String dataSql = "select t1.Date, t1.sumOfNumStat, (t1.NumSumAvg+t1.NumSumThreshold*t1.NumSumStdDev) as upperLimit, (t1.NumSumAvg-t1.NumSumThreshold*t1.NumSumStdDev) as lowerLimit from  "
						+ tableName + " t1 join (select Date,max(Run) as Run from " + tableName + " where idApp="
						+ idApp + " and Date<='" + processedDate + "' group by Date) t2 on t1.Date<='" + processedDate
						+ "' and t1.Date=t2.Date and t1.Run = t2.Run and t1.dGroupVal='" + microsegmentValue
						+ "' and t1.dGroupCol='" + microsegment + "' and t1.ColName='" + columnName + "' and t1.idApp="
						+ idApp + " order by Date desc limit 10";
				LOG.debug("dataSql: " + dataSql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckMicrosegmentReport>() {
					@Override
					public TrendCheckMicrosegmentReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckMicrosegmentReport trendCheckMicroSegmentReport = new TrendCheckMicrosegmentReport();
						trendCheckMicroSegmentReport.setDate(rs.getString("Date"));
						trendCheckMicroSegmentReport.setSumOfNumstat(rs.getDouble("sumOfNumStat"));
						trendCheckMicroSegmentReport.setLowerLimit(rs.getDouble("lowerLimit"));
						trendCheckMicroSegmentReport.setUpperLimit(rs.getDouble("upperLimit"));
						return trendCheckMicroSegmentReport;
					}
				});

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public TrendCheckDataDriftReport getTrendCheckDataDriftSummary(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName) {

		TrendCheckDataDriftReport trendCheckDataDriftReport = new TrendCheckDataDriftReport();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = null;
			try {
				idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);
			} catch (Exception e) {
				
				LOG.error("Failed to get the idApp !!");
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			if (idApp != null) {
				String tableName = "DATA_QUALITY_DATA_DRIFT_SUMMARY";

				String newValuesSql = "select uniqueValues from " + tableName + " where idApp="+idApp+" and Date='" + processedDate
						+ "' and Run=(select max(Run) from " + tableName + " where idApp="+idApp+" and Date='" + processedDate
						+ "') and dGroupVal='" + microsegmentValue + "' and dGroupCol='" + microsegment
						+ "' and colName='" + columnName + "' and Operation='New'";
				LOG.debug("newValuesSql: " + newValuesSql);

				List<String> newValuesList = jdbcTemplate1.queryForList(newValuesSql, String.class);

				String missingValuesSql = "select uniqueValues from " + tableName + " where idApp="+idApp+" and Date='"
						+ processedDate + "' and Run=(select max(Run) from " + tableName + " where idApp="+idApp+" and Date='"
						+ processedDate + "') and dGroupVal='" + microsegmentValue + "' and dGroupCol='" + microsegment
						+ "' and colName='" + columnName + "' and Operation='Missing'";
				LOG.debug("missingValuesSql: " + missingValuesSql);

				List<String> missingValuesList = jdbcTemplate1.queryForList(missingValuesSql, String.class);

				trendCheckDataDriftReport.setNewUniqueValues(newValuesList);
				trendCheckDataDriftReport.setMissingUniqueValues(missingValuesList);
			}
		} catch (Exception e) {
			LOG.error("Failed to get the idApp !!");
			e.printStackTrace();
		}
		return trendCheckDataDriftReport;
	}

	@Override
	public List<TrendCheckTrendGraphReport> getTrendCheckAverageTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName) {

		List<TrendCheckTrendGraphReport> resultList = new ArrayList<TrendCheckTrendGraphReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = null;
			try {
				idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);
			} catch (Exception e) {
				LOG.error("Failed to get the idApp !!");
				LOG.error("exception "+e.getMessage());				
				e.printStackTrace();
			}
			if (idApp != null) {
				String tableName = "DATA_QUALITY_Column_Summary";

				String dataSql = "select t1.Date, t1.Mean, (t1.NumMeanAvg+t1.NumMeanThreshold*NumMeanStdDev) as upperLimit, (t1.NumMeanAvg-t1.NumMeanThreshold*NumMeanStdDev) as lowerLimit from  "
						+ tableName + " t1 join (select Date,max(Run) as Run from " + tableName + " where idApp="
						+ idApp + " and Date<='" + processedDate + "' group by Date) t2 on t1.Date<='" + processedDate
						+ "' and t1.Date=t2.Date and t1.Run = t2.Run and t1.dGroupVal='" + microsegmentValue
						+ "' and t1.dGroupCol='" + microsegment + "' and t1.ColName='" + columnName + "' and t1.idApp="
						+ idApp + " order by Date desc limit 10";
				LOG.debug("dataSql: " + dataSql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckTrendGraphReport>() {
					@Override
					public TrendCheckTrendGraphReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckTrendGraphReport trendCheckMicroSegmentReport = new TrendCheckTrendGraphReport();
						trendCheckMicroSegmentReport.setDate(rs.getString("Date"));
						trendCheckMicroSegmentReport.setValue(rs.getDouble("Mean"));
						trendCheckMicroSegmentReport.setLowerLimit(rs.getDouble("lowerLimit"));
						trendCheckMicroSegmentReport.setUpperLimit(rs.getDouble("upperLimit"));
						return trendCheckMicroSegmentReport;
					}
				});

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<TrendCheckTrendGraphReport> getTrendCheckDistributionTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName) {

		List<TrendCheckTrendGraphReport> resultList = new ArrayList<TrendCheckTrendGraphReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = null;
			try {
				idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);
			} catch (Exception e) {
				LOG.error("Failed to get the idApp !!");
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			if (idApp != null) {
				String tableName = "DATA_QUALITY_Column_Summary";

				String dataSql = "select t1.Date, t1.Std_Dev, (t1.NumSDAvg+t1.NumSDThreshold*NumSDStdDev) as upperLimit, (t1.NumSDAvg-t1.NumSDThreshold*NumSDStdDev) as lowerLimit from  "
						+ tableName + " t1 join (select Date,max(Run) as Run from " + tableName + " where idApp="
						+ idApp + " and Date<='" + processedDate + "' group by Date) t2 on t1.Date<='" + processedDate
						+ "' and t1.Date=t2.Date and t1.Run = t2.Run and t1.dGroupVal='" + microsegmentValue
						+ "' and t1.dGroupCol='" + microsegment + "' and t1.ColName='" + columnName + "' and t1.idApp="
						+ idApp + " order by Date desc limit 10";
				LOG.debug("dataSql: " + dataSql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckTrendGraphReport>() {
					@Override
					public TrendCheckTrendGraphReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckTrendGraphReport trendCheckMicroSegmentReport = new TrendCheckTrendGraphReport();
						trendCheckMicroSegmentReport.setDate(rs.getString("Date"));
						trendCheckMicroSegmentReport.setValue(rs.getDouble("Std_Dev"));
						trendCheckMicroSegmentReport.setLowerLimit(rs.getDouble("lowerLimit"));
						trendCheckMicroSegmentReport.setUpperLimit(rs.getDouble("upperLimit"));
						return trendCheckMicroSegmentReport;
					}
				});

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<TrendCheckTrendGraphReport> getTrendCheckSumTrend(String processedDate, String sourceType,
			String source, String fileName, Integer locationId, String microsegment, String microsegmentValue,
			String columnName) {

		List<TrendCheckTrendGraphReport> resultList = new ArrayList<TrendCheckTrendGraphReport>();

		try {
			String sql = "select distinct idApp from locationMapping where sourceType=? and source=? and fileName=? and locationId=? limit 1";
			Long idApp = null;
			try {
				idApp = jdbcTemplate.queryForObject(sql, Long.class, sourceType, source, fileName, locationId);
			} catch (Exception e) {
				LOG.error("Failed to get the idApp !!");
				LOG.error("exception "+e.getMessage());
				e.printStackTrace();
			}
			if (idApp != null) {
				String tableName = "DATA_QUALITY_Column_Summary";

				String dataSql = "select t1.Date, t1.sumOfNumStat, (t1.NumSumAvg+t1.NumSumThreshold*t1.NumSumStdDev) as upperLimit, (t1.NumSumAvg-t1.NumSumThreshold*t1.NumSumStdDev) as lowerLimit from  "
						+ tableName + " t1 join (select Date,max(Run) as Run from " + tableName + " where idApp="
						+ idApp + " and Date<='" + processedDate + "' group by Date) t2 on t1.Date<='" + processedDate
						+ "' and t1.Date=t2.Date and t1.Run = t2.Run and t1.dGroupVal='" + microsegmentValue
						+ "' and t1.dGroupCol='" + microsegment + "' and t1.ColName='" + columnName + "' and t1.idApp="
						+ idApp + " order by Date desc limit 10";
				LOG.debug("dataSql: " + dataSql);

				resultList = jdbcTemplate1.query(dataSql, new RowMapper<TrendCheckTrendGraphReport>() {
					@Override
					public TrendCheckTrendGraphReport mapRow(ResultSet rs, int rowNum) throws SQLException {
						TrendCheckTrendGraphReport trendCheckMicroSegmentReport = new TrendCheckTrendGraphReport();
						trendCheckMicroSegmentReport.setDate(rs.getString("Date"));
						trendCheckMicroSegmentReport.setValue(rs.getDouble("sumOfNumStat"));
						trendCheckMicroSegmentReport.setLowerLimit(rs.getDouble("lowerLimit"));
						trendCheckMicroSegmentReport.setUpperLimit(rs.getDouble("upperLimit"));
						return trendCheckMicroSegmentReport;
					}
				});
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

}
