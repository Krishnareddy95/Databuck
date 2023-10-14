package com.databuck.service;

import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import com.databuck.dao.IValidationCheckDAO;
import org.apache.log4j.Logger;

@Service
public class QuickStartService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ITaskDAO taskDao;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private IValidationCheckDAO validationCheckDao;
	
	private static final Logger LOG = Logger.getLogger(QuickStartService.class);

	public JSONObject getQuickStartValidationDetailsByIdData(long idData, String username, String enableMicrosegment) {

		String status = "failed";
		String message = "";
		JSONObject jsonObject1 = new JSONObject();
		String uniqueId = "";
		String validationRunType = DatabuckConstants.VAL_RUN_TYPE_FULL_LOAD;
		JSONObject jsonObject = new JSONObject();
		try {
			// Get deployMode
			String deployMode = clusterProperties.getProperty("deploymode");

			if (deployMode.trim().equalsIgnoreCase("2")) {
				deployMode = "local";
			} else {
				deployMode = "cluster";
			}

			String templateName = validationCheckDao.getNameFromListDataSources(idData);

			// validate template idData
			if (templateName != null && !templateName.isEmpty()) {

				// Get latest validation
				Long nonmicrosegment_idApp = validationCheckDao.getMaxValidationByIdData(idData);
				Long microsegment_idApp = validationCheckDao.getMaxMicrosegmentValidationByIdData(idData);

				Long idApp = nonmicrosegment_idApp;
				if (enableMicrosegment != null && enableMicrosegment.trim().equalsIgnoreCase("Y")
						&& microsegment_idApp != null && microsegment_idApp > 0l) {
					idApp = microsegment_idApp;
				}
				
				if (idApp != null && idApp > 0l) {

					ListApplications listAppl = validationCheckDao.getdatafromlistapplications(idApp);

					String validationName = listAppl.getName();

					// to check job in queued or in-progress or starting
					uniqueId = taskDao.getMaxUniqueIdByIdApp(idApp);

					if (uniqueId != null && !uniqueId.isEmpty()) {

						LOG.info("\n====> Validtion is already queued or in-progress.");
						message = "Validtion is already queued or in-progress.";
						jsonObject1.put("idApp", idApp);
						jsonObject1.put("validationName", validationName);
						jsonObject1.put("uniqueId", uniqueId);
						status = "success";
					} else {
						// Place the job in queue
						uniqueId = taskDao.insertRunScheduledTask(idApp, "queued", deployMode, username,
								validationRunType);
						LOG.debug("\n====> Validation [" + idApp + "] with uniqueId: " + uniqueId +"is placed in queue");
						message = "Validation [" + idApp + "] is placed in queue";
						status = "success";
						jsonObject1.put("uniqueId", uniqueId);
					}
				} else
					message = "Validation does not exist for template with id["+ idData +"]";

			} else
				message = "Invalid template Id";

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		jsonObject.put("result", jsonObject1);
		jsonObject.put("message", message);
		jsonObject.put("status", status);

		return jsonObject;

	}

}
