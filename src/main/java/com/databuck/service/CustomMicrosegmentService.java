package com.databuck.service;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.CustomMicrosegment;
import com.databuck.dao.CustomMicrosegmentDao;
import com.databuck.dao.ITemplateViewDAO;

@Service
public class CustomMicrosegmentService {

	@Autowired
	CustomMicrosegmentDao customMicrosegmentDao;

	@Autowired
	ITemplateViewDAO templateViewDAO;
	
	private static final Logger LOG = Logger.getLogger(CustomMicrosegmentService.class);

	/*
	 * Service method to call add method to insert details to table
	 * 'custom_microsegment_details'
	 */
	public JSONObject addCustomMicrosegments(CustomMicrosegment customMicrosegment) {
		
		LOG.debug("customMicrosegment "+customMicrosegment);

		JSONObject resultObj = new JSONObject();
		String status = "failed";
		String message = "";
		try {
			boolean isDuplicate = customMicrosegmentDao.isDuplicateCustomMicrosegments(customMicrosegment);
			if (!isDuplicate) {

				if (customMicrosegmentDao.addCustomMicrosegments(customMicrosegment)) {
					message = "Custom Microsegment Added successfully";
					status = "success";
				} else
					message = "Failed to Add custom Microsegment";
			} else
				message = "Microsegments combination Already Exists";

		} catch (Exception e) {			
			message = "Exception occurred while adding Custom Microsegment";
			e.printStackTrace();
		}
		resultObj.put("status", status);
		resultObj.put("message", message);
		return resultObj;
	}

}
