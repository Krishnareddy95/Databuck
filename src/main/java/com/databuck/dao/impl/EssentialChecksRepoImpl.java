package com.databuck.dao.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.csvmodel.NullCheck;
import com.databuck.dao.IEssentialChecksRepo;

@Repository
public class EssentialChecksRepoImpl implements IEssentialChecksRepo {

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	
	private static final Logger LOG = Logger.getLogger(EssentialChecksRepoImpl.class);

	@Override
	public List<NullCheck> getNullChecks(Long idApp, String fromDate, String toDate, String checkName, String tableName)
			throws Exception {
		try {
			String sql = "select * from " + tableName + " where idApp = " + idApp + " and Date between '" + fromDate
					+ "' and '" + toDate + "'  limit 100000";
			
			LOG.debug("sql "+sql);
			List<NullCheck> checks = new ArrayList<>();
			SqlRowSet rs = jdbcTemplate1.queryForRowSet(sql);
			while (rs.next()) {
				NullCheck check = new NullCheck();
				Date exc_Date = rs.getDate("Date");
				String execDate = null;
				if (exc_Date != null) {
					execDate = new SimpleDateFormat("yyyy-MM-dd").format(exc_Date);
					check.setExecDate(execDate);
					check.setRun(rs.getInt("Run"));
				}
				check.setStatus(rs.getString("Status"));
				check.setColumnName(rs.getString("ColName"));
				check.setTotalRecords(rs.getInt("Record_Count"));
				check.setHistoricNullStatus(rs.getString("Historic_Null_Status"));
				Double histMeanDb = rs.getDouble("Historic_Null_Mean");
				String histMean = "";
				if (histMeanDb != null)
					histMean = decimalFormat.format(histMeanDb);
				check.setHistoricNullMean(histMean);
				Double nullThresholdDb = rs.getDouble("Null_Threshold");
				String nullThreshold = "";
				if (nullThresholdDb != null)
					nullThreshold = decimalFormat.format(nullThresholdDb);
				check.setNullThreshold(nullThreshold);
				String nullStdDev = "";
				Double nullStdDevDb = rs.getDouble("Historic_Null_stddev");
				if (nullStdDevDb != null)
					nullStdDev = decimalFormat.format(nullStdDevDb);
				check.setHistoricNullStdDev(nullStdDev);
				String nullPercentage = "";
				Double nullPercentageDb = rs.getDouble("Null_Percentage");
				if (nullPercentageDb != null)
					nullPercentage = decimalFormat.format(nullPercentageDb);
				check.setNullPercentage(nullPercentage);
				check.setNullValue(rs.getInt("Null_Value"));
				checks.add(check);
			}
			return checks;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
			throw new Exception("Records not found", e.getCause());
		}
	}

}
