package com.databuck.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.databuck.bean.CustomMicrosegment;
import com.databuck.config.DatabuckEnv;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.CustomMicrosegmentDao;

@Repository
public class CustomMicrosegmentDaoImpl implements CustomMicrosegmentDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(CustomMicrosegmentDaoImpl.class);

	@Override
	public List<String> getCustomMicroSegmentsColumnNamesForCheck(long idData, String checkName) {
		try {
			// Query compatibility changes for both POSTGRES and MYSQL
			String sql;
			if ((DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES))) {
				sql = "select displayName from listDataDefinition where idData=" + idData
						+ " and (trim(lower(format)) = \'integer\' or trim(lower(format)) = \'string\') order by displayName";

			} else
				sql = "select displayName from listDataDefinition where idData=" + idData
						+ " and (trim(lower(format)) = \"integer\" or trim(lower(format)) = \"string\") order by displayName";

			
			LOG.debug(sql);
			List<String> listColumnNames = jdbcTemplate.query(sql, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("displayName");
				}
			});
			return listColumnNames;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}

	@Override
	public List<CustomMicrosegment> getCustomMicroSegmentsByIdData(long idData) {
		List<CustomMicrosegment> customMicrosegments = null;
		try {

			String sql = "select id,template_id,check_name,microsegment_columns,check_columns from custom_microsegment_details where template_id="
					+ idData;

			LOG.debug(sql);
			customMicrosegments = jdbcTemplate.query(sql, new RowMapper<CustomMicrosegment>() {

				@Override
				public CustomMicrosegment mapRow(ResultSet rs, int rowNum) throws SQLException {
					CustomMicrosegment custMicroseg = new CustomMicrosegment();
					custMicroseg.setId(rs.getLong("id"));
					custMicroseg.setCheckEnabledColumns(rs.getString("check_columns"));
					custMicroseg.setCheckName(rs.getString("check_name"));
					custMicroseg.setMicrosegmentColumns(rs.getString("microsegment_columns"));
					custMicroseg.setTemplateId(rs.getLong("template_id"));
					return custMicroseg;
				}
			});

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return customMicrosegments;
	}

	@Override
	public boolean addCustomMicrosegments(CustomMicrosegment customMicrosegment) {
		try {
			String sql = "insert into custom_microsegment_details(template_id,check_name,microsegment_columns,check_columns)"
					+ "values (?,?,?,?)";
			LOG.debug(sql);
			
			jdbcTemplate.update(sql, customMicrosegment.getTemplateId(), customMicrosegment.getCheckName(),
					customMicrosegment.getMicrosegmentColumns(), customMicrosegment.getCheckEnabledColumns());
			return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isDuplicateCustomMicrosegments(CustomMicrosegment customMicrosegment) {
		long idData = customMicrosegment.getTemplateId();
		String microSegments = customMicrosegment.getMicrosegmentColumns();

		String origionalMicroSegments = "";
		try {// Query compatibility changes for both POSTGRES and MYSQL
			String sql = "";
			if (DatabuckEnv.DB_TYPE.equalsIgnoreCase(DatabuckConstants.DB_TYPE_POSTGRES)) {
				sql = "select string_agg(TRIM(displayName),',')as displayName from listDataDefinition where idData="
						+ idData + " and dgroup='Y'";
			} else {
				sql = "select GROUP_CONCAT(TRIM(displayName)) as displayName from listDataDefinition where idData="
						+ idData + " and dgroup='Y'";
			}
			LOG.debug(sql);
			origionalMicroSegments = jdbcTemplate.queryForObject(sql, String.class);
			if (origionalMicroSegments == null
					|| !microSegments.trim().equalsIgnoreCase(origionalMicroSegments.trim())) {

				sql = "select count(*) from custom_microsegment_details where template_id=" + idData
						+ " and LOWER(microsegment_columns) = '" + microSegments + "'";

				LOG.debug(sql);
				int count = jdbcTemplate.queryForObject(sql, Integer.class);

				if (count <= 0)
					return false;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteCustomMicrosegments(long microsegmentRowId) {
		try {

			String sql = "delete from custom_microsegment_details where id=" + microsegmentRowId;
			int update = jdbcTemplate.update(sql);

			if (update > 0)
				return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

}
