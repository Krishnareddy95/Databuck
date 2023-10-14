package com.databuck.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.DatabuckTagsDao;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.RuleCatalogDao;
import org.apache.log4j.Logger;

@Service
public class DatabuckTagsService {
	@Autowired
	private GlobalRuleDAO globalRuleDAO;

	@Autowired
	private DatabuckTagsDao databuckTagsDao;
	
	@Autowired
	private IValidationCheckDAO validationCheckDAO;
	
	@Autowired
	private RuleCatalogDao ruleCatalogDao;
	
	private static final Logger LOG = Logger.getLogger(DatabuckTagsService.class);

	public JSONObject addDatabuckTags(String tagName, String description) {
		JSONObject resJson = new JSONObject();
		String message = "";
		String status = "failed";
		long tagId=0l;
		try {
			if (databuckTagsDao.isTagDuplicate(tagName)) {
				message = "Failed to add, Tag name is duplicate.";
			} else {
				tagId = databuckTagsDao.addDatabuckTags(tagName, description);
				message = "Databuck tag created successfully";
				status = "success";
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		resJson.put("status", status);
		resJson.put("message", message);
		resJson.put("tagId", tagId);
		return resJson;
	}
	
	public JSONObject linkDatabuckTagToRule(int ruleId, int tagId, int idApp) {
		LOG.info("linkDatabuckTagsToRule - START");

		JSONObject resJson = new JSONObject();
		String message = "";
		String status = "failed";
		int rowId = 0;
		boolean linkStatus = false;

		LOG.info("Get the validation approval status ..");
		String approvalStatus = ruleCatalogDao.getValidationApprovalStatus(idApp);

		try {
			if (approvalStatus != null
					&& approvalStatus.trim().equalsIgnoreCase(DatabuckConstants.RULE_CATALOG_RUNNABLE_STATUS_2)) {
				rowId = ruleCatalogDao.getRowIdFromStagingValidation(idApp, ruleId);
			} else
				rowId = ruleCatalogDao.getRowIdFromActualValidation(idApp, ruleId);

			if (rowId > 0) {

				if (tagId > 0) {
					boolean isTagLinked = databuckTagsDao.isDatabuckTagLinkedToRule(ruleId, tagId, idApp);

					if (!isTagLinked) {
						
						linkStatus = databuckTagsDao.linkDatabuckTagsToRule(tagId, ruleId, idApp);
					} else
						linkStatus = true;

					if (linkStatus) {
						message = "Databuck Tag attached successfully";
						status = "success";
					} else
						message = "Failed to link Tag to Rule";

				} else {
					message = "Invalid TagId";
				}
			} else {
				message = "Invalid Rule";
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		resJson.put("status", status);
		resJson.put("message", message);
		return resJson;
	}

	public JSONObject linkDatabuckTagToRule(int ruleId, JSONArray tagIdList, int idApp) {
		LOG.info("linkDatabuckTagsToRule - START");

		JSONObject resJson = new JSONObject();
		String message = "";
		String status = "failed";
		long valId = idApp;
		boolean linkStatus = false;
		try {
			// Validate idApp
			ListApplications listApplications = validationCheckDAO.getdatafromlistapplications(valId);
			if (listApplications != null) {

				if (tagIdList != null && tagIdList.length() > 0) {
					// deleting previous entry
					databuckTagsDao.deleteDatabuckTagsFromRuleTagMapping(ruleId, idApp);
					// linking tags to rules
					for (Object tagId : tagIdList) {
						linkStatus = databuckTagsDao.linkDatabuckTagsToRule((Integer) tagId, ruleId, idApp);
					}
					if (linkStatus) {
						message = "Databuck Tags linked successfully";
						status = "success";
					} else
						message = "Failed to link Tag to Rule";
				}else{
					// delink tags
					databuckTagsDao.deleteDatabuckTagsFromRuleTagMapping(ruleId, idApp);
					message = "No Tags are linked with Rule";
					status = "success";
				}
			} else
				message = "Invalid validationId";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		resJson.put("status", status);
		resJson.put("message", message);
		return resJson;
	}


}
