package com.databuck.dao;

import java.util.List;

import org.json.JSONArray;

import com.databuck.bean.DatabuckTags;
import org.json.JSONObject;

public interface DatabuckTagsDao {

	public JSONArray getAllDatabuckTags();

	public int updateDatabuckTagInfo(int tagId, String tagName, String description);

	public long addDatabuckTags(String tagName, String description);

	public List<DatabuckTags> getDatabuckTags();

	public boolean isTagDuplicate(String tagName);
	
	public boolean isDatabuckTagLinkedToRule(int ruleId, int tagId, int idApp);

	public boolean linkDatabuckTagsToRule(int tagId, int ruleId, int idApp);

	public void deleteDatabuckTagsFromRuleTagMapping(int ruleId, int idApp);

	public JSONArray getAllLinkedTags(long idApp, int ruleId);
}
