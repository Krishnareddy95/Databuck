package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.databuck.bean.listGlobalThresholds;
import com.databuck.dao.IGlobalThresholdDAO;

@Repository
public class GlobalThresholdDAOImpl implements IGlobalThresholdDAO {

	@Autowired
	public JdbcTemplate jdbcTemplate;


	public List<listGlobalThresholds> view() {

		String sSqlQry = "";

		sSqlQry = sSqlQry + "select a.*, b.domainName\n";
		sSqlQry = sSqlQry + "from listGlobalThresholds a, domain b\n";
		sSqlQry = sSqlQry + "where a.domainId = b.domainId\n";
		sSqlQry = sSqlQry + "order by b.domainName, a.globalColumnName;";

		List<listGlobalThresholds> listGlobalThresholds = jdbcTemplate.query(sSqlQry, new RowMapper<listGlobalThresholds>() {

			@Override
			public listGlobalThresholds mapRow(ResultSet rs, int rowNum) throws SQLException {

				listGlobalThresholds listGlobalThresholds = new listGlobalThresholds();
				
				listGlobalThresholds.setDomainId(rs.getInt("domainId"));
				listGlobalThresholds.setDomainName(rs.getString("domainName"));				
				listGlobalThresholds.setGlobalColumnName(rs.getString("globalColumnName"));
				listGlobalThresholds.setDescription(rs.getString("description"));
				listGlobalThresholds.setNullCountThreshold(rs.getDouble("nullCountThreshold"));
				listGlobalThresholds.setNumericalThreshold(rs.getDouble("numericalThreshold"));
				listGlobalThresholds.setStringstatThreshold(rs.getDouble("stringstatThreshold"));
				listGlobalThresholds.setDataDriftThreshold(rs.getDouble("dataDriftThreshold"));
				listGlobalThresholds.setRecordAnomalyThreshold(rs.getDouble("recordAnomalyThreshold"));
				listGlobalThresholds.setLengthCheckThreshold(rs.getDouble("lengthCheckThreshold"));

				return listGlobalThresholds;
			}

		});
		return listGlobalThresholds;

	}


}
