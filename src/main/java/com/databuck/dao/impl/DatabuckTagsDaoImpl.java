package com.databuck.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.databuck.bean.DatabuckTags;
import com.databuck.dao.DatabuckTagsDao;

@Repository
public class DatabuckTagsDaoImpl implements DatabuckTagsDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private static final Logger LOG = Logger.getLogger(DatabuckTagsDaoImpl.class);

	@Override
	public JSONArray getAllDatabuckTags() {
		JSONArray jsonArray = new JSONArray();

		try {
			String sql = "select * from databuck_tags";
			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);

			while (sqlRowSet.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("tagId", sqlRowSet.getInt("tag_id"));
				jsonObject.put("tagName", sqlRowSet.getString("tag_name"));
				jsonObject.put("description", sqlRowSet.getString("description"));
				jsonArray.put(jsonObject);

			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}
	
	@Override
	public List<DatabuckTags> getDatabuckTags() {

		List<DatabuckTags> databuckTags = new ArrayList<DatabuckTags>();
		try {
			String sql = "select * from databuck_tags";
			LOG.debug(sql);
			databuckTags = jdbcTemplate.query(sql,new RowMapper<DatabuckTags>(){
				public DatabuckTags mapRow(ResultSet rs, int rowNum) throws SQLException {
					DatabuckTags tags = new DatabuckTags();
					tags.setTagId(rs.getInt("tag_id"));
					tags.setTagName(rs.getString("tag_name"));
					tags.setDescription(rs.getString("description"));
					
					return tags;
				}
			});
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return databuckTags;
	}

	@Override
	public int updateDatabuckTagInfo(int tagId, String tagName, String description) {
		try {
			String sql = "update databuck_tags set tag_name=? , description=? where tag_id=?";
			LOG.debug(sql);
			int update = jdbcTemplate.update(sql, tagName, description, tagId);
			return update;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long addDatabuckTags(String tagName, String description) {
		long result = 0;
		try {
			String sql = "insert into databuck_tags(tag_name,description) values(?,?)";
			LOG.debug(sql);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement pst = con.prepareStatement(sql, new String[] { "tag_id" });
					pst.setString(1, tagName);
					pst.setString(2, description);
					return pst;
				}
			}, keyHolder);

			result = keyHolder.getKey().longValue();

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return result;

	}
	public boolean isTagDuplicate(String tagName){
		boolean status = false;
		try {
			String sql = "select count(*) from databuck_tags where tag_name=?";
			LOG.debug(sql);
			Long duplicateCount = jdbcTemplate.queryForObject(sql, Long.class, tagName);
			if (duplicateCount != null && duplicateCount > 0) {
				status = true;
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}

	@Override
	public boolean isDatabuckTagLinkedToRule(int ruleId, int tagId, int idApp) {
		boolean status = false;
		try {
			String sql = "select count(*) from  rule_tag_mapping where tag_id=? and rule_id=? and idApp=?";
			LOG.debug(sql);
			int count = jdbcTemplate.queryForObject(sql, Integer.class, tagId, ruleId, idApp);
			if (count > 0)
				status = true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return status;
	}
	
	@Override
	public boolean linkDatabuckTagsToRule(int tagId, int ruleId, int idApp) {
		try {
			String sql = "insert into rule_tag_mapping(tag_id,rule_id,idApp) values (?,?,?)";
			LOG.debug(sql);
			jdbcTemplate.update(sql, tagId, ruleId, idApp);
			return true;
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void deleteDatabuckTagsFromRuleTagMapping(int ruleId, int idApp) {
		try {
			String sql = "delete from rule_tag_mapping where rule_id=? and idApp=?";
			LOG.debug(sql);
			jdbcTemplate.update(sql, ruleId, idApp);

		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public JSONArray getAllLinkedTags(long idApp, int ruleId) {
		JSONArray linkedTagsList = new JSONArray();
		try {
			String sql = "select dm.tag_id, dt.tag_name from rule_tag_mapping dm join databuck_tags dt on dt.tag_id = dm.tag_id where dm.idApp = ? and dm.rule_id = ?";
			LOG.debug(sql);
			SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, idApp, ruleId);
			while (sqlRowSet.next()) {
				JSONObject linkedTags = new JSONObject();
				linkedTags.put("tagId", sqlRowSet.getInt("tag_id"));
				linkedTags.put("tagName", sqlRowSet.getString("tag_Name"));
				linkedTagsList.put(linkedTags);
			}
		} catch (Exception e) {
			LOG.error("exception "+e.getMessage());
			e.printStackTrace();
		}
		return linkedTagsList;
	}

}
