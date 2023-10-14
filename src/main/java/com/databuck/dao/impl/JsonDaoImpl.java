package com.databuck.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.dao.JsonDaoI;
import com.databuck.bean.ListDfTranRule;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;

@Repository
public class JsonDaoImpl implements JsonDaoI {

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(JsonDaoImpl.class);

	@Override
	public Map<String, String> getDateAndRunForSummaryOfLastRun(Long idApp) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String sql = "select max(Date) as Date,max(Run) as Run from DATA_QUALITY_Transactionset_sum_A1 where idApp=? and Date=(select max(Date) from DATA_QUALITY_Transactionset_sum_A1 where idApp=?)";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql, idApp, idApp);
			while (queryForRowSet.next()) {
				map.put("Date", queryForRowSet.getString(1));
				map.put("Run", queryForRowSet.getString(2));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public double getDQIByCheckName(Long idApp, String date, String run, String testName) {
		double score = 0.0;
		try {
			String sql = "Select DQI from DashBoard_Summary where AppId=" + idApp + " and Date='" + date + "' and Run="
					+ run + " and Test='" + testName + "' limit 1";
			Double dqi = jdbcTemplate1.queryForObject(sql, Double.class);
			if (dqi != null) {
				score = dqi;
			}
		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return score;
	}

	@Override
	public double getAggregateDQIForDataQualityDashboard(Long idApp, String date, String run) {
		double score = 0.0;
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if(DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "Select avg(DQI) from DashBoard_Summary where Status is NOT NULL and length(trim(COALESCE(Status,''))) > 0  and AppId="
					+ idApp + " and Date='" + date + "' and Run=" + run;
			} else {
				sql = "Select avg(DQI) from DashBoard_Summary where Status is NOT NULL and length(trim(ifnull(Status,''))) > 0  and AppId="
						+ idApp + " and Date='" + date + "' and Run=" + run;
			}
			Double dqi = jdbcTemplate1.queryForObject(sql, Double.class);
			if (dqi != null) {
				score = dqi;
			}
		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return score;
	}

	@Override
	public List<ListDfTranRule> getDataFromListDfTranRule(Long idApp) {
		RowMapper<ListDfTranRule> rm = (rs, i) -> {
			ListDfTranRule ldf = new ListDfTranRule();
			ldf.setIdDFT(rs.getLong("idDFT"));
			ldf.setIdApp(rs.getLong("idApp"));
			ldf.setDupRow(rs.getString("dupRow"));
			ldf.setSeqRow(rs.getString("seqRow"));
			ldf.setSeqIDcol(rs.getLong("seqIDcol"));
			ldf.setThreshold(rs.getDouble("threshold"));
			ldf.setType(rs.getString("type"));
			return ldf;
		};
		return jdbcTemplate.query("SELECT * FROM listDFTranRule WHERE idApp=?", rm, idApp);
	}
}
