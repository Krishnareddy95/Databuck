package com.databuck.dao.impl;

import com.databuck.bean.MappingDetail;
import com.databuck.dao.MappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

@Repository
public class MappingDAOImpl implements MappingDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate1;
	
	private static final Logger LOG = Logger.getLogger(MappingDAOImpl.class);

	@Override
	public List<MappingDetail> getMappingEntries(String refMappingTableName, String dataTableName) {
		try {
			String q = "select * from " + refMappingTableName + " where table_name='"+dataTableName+"'";
			RowMapper<MappingDetail> rowMapper = (rs, i) -> {
				MappingDetail mappingDetail = new MappingDetail();
				mappingDetail.setDbkRowId(rs.getLong("dbk_row_Id"));

				String table_name = rs.getString("table_name");
				if (table_name != null)
					table_name = table_name.trim();
				mappingDetail.setTableName(table_name);

				String left_column = rs.getString("left_column");
				if (left_column != null)
					left_column = left_column.trim();
				mappingDetail.setLeftColumn(left_column);

				String right_column = rs.getString("right_column");
				if (right_column != null)
					right_column = right_column.trim();
				mappingDetail.setRightColumn(right_column);

				String default_value = rs.getString("default_value");
				if (default_value != null)
					default_value = default_value.trim();
				mappingDetail.setDefaultValue(default_value);

				mappingDetail.setPosition(rs.getLong("position"));
				LOG.debug(mappingDetail);
				return mappingDetail;
			};
			List<MappingDetail> displayNames = jdbcTemplate1.query(q, rowMapper);
			return displayNames;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, String> getMatchedLeftColumnMappingDetails(String refMappingTableName, String dataTableName, String microsegColsStr) {
		try {
			String sql = "select * from " + refMappingTableName + " where left_column in ('" + microsegColsStr
					+ "') and right_column!='' and table_name='"+dataTableName+"'";
			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Map<String, String> map = new HashMap<String, String>();
			while (queryForRowSet.next()) {
				String left_column = queryForRowSet.getString("left_column");
				if (left_column != null)
					left_column = left_column.trim();

				String right_column = queryForRowSet.getString("right_column");
				if (right_column != null)
					right_column = right_column.trim();

				map.put(left_column, right_column);
			}
			return map;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, String> getUnmatchedLeftColumnMappingDetails(String refMappingTableName, String dataTableName,
			String microsegColsStr) {
		try {
			String sql = "select * from " + refMappingTableName + " where left_column not in ('" + microsegColsStr
					+ "', '') and table_name='"+dataTableName+"'";

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(sql);
			Map<String, String> map = new HashMap<String, String>();
			while (queryForRowSet.next()) {
				String left_column = queryForRowSet.getString("left_column");
				if (left_column != null)
					left_column = left_column.trim();

				String right_column = queryForRowSet.getString("right_column");
				if (right_column != null)
					right_column = right_column.trim();

				map.put(left_column, right_column);
			}
			return map;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			// e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, String> getCompulsoryColumnMappingDetails(String refMappingTableName, String dataTableName) {
		try {

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(
					"select right_column,default_value from " + refMappingTableName + " where left_column='' and table_name='"+dataTableName+"'");
			Map<String, String> map = new HashMap<String, String>();
			while (queryForRowSet.next()) {
				String right_Column = queryForRowSet.getString("right_column");
				if (right_Column != null)
					right_Column = right_Column.trim();

				String default_value = queryForRowSet.getString("default_value");
				if (default_value != null)
					default_value = default_value.trim();

				map.put(right_Column, default_value);
			}
			return map;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getHeadersForMappingDetails(String refMappingTableName, String dataTableName) {
		try {

			SqlRowSet queryForRowSet = jdbcTemplate1.queryForRowSet(
					"select right_column,default_value from " + refMappingTableName + " where table_name='"+dataTableName+"' order by position asc");
			List<String> headers = new ArrayList<>();
			while (queryForRowSet.next()) {
				String right_Column = queryForRowSet.getString("right_column");
				if (right_Column != null)
					right_Column = right_Column.trim();
				headers.add(right_Column);
			}
			return headers;

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
