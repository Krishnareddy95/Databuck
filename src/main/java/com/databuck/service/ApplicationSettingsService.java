package com.databuck.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ApplicationSettingsUpdate;
import com.databuck.dao.ITaskDAO;
import com.databuck.econstants.DatabuckPropertyCategory;
import org.apache.log4j.Logger;

@Service
public class ApplicationSettingsService {

	@Autowired
	public ITaskDAO taskDao;

	@Autowired
	private Properties resultDBConnectionProperties;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private Properties clusterProperties;

	@Autowired
	private Properties activeDirectoryConnectionProperties;

	@Autowired
	private Properties mongoDbProperties;

	@Autowired
	private Properties dbDependencyProperties;

	@Autowired
	private Properties integrationProperties;

	@Autowired
	private Properties cdpProperties;

	@Autowired
	private Properties gcpProperties;

	@Autowired
	private Properties azureProperties;

	@Autowired
	private Properties maprProperties;
	
	private static final Logger LOG = Logger.getLogger(ApplicationSettingsService.class);

	public JSONObject saveUpdatedProperties(ApplicationSettingsUpdate[] paramJsonValues) {
		LOG.info("saveUpdatedProperties-Start");
		String message = "";
		String status = "failed";
		String isRestartRequired = "N";
		List<String> updateFailedProps = new ArrayList<String>();

		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

			if (paramJsonValues.length > 0) {

				List<String> propertyCategoriesNames = Stream.of(DatabuckPropertyCategory.values()).map(Enum::name)
						.collect(Collectors.toList());

				for (ApplicationSettingsUpdate paramProperty : paramJsonValues) {
					String propertyCategoryName = paramProperty.getPropName();
					String propertyName = paramProperty.getPropKeys();
					String propertyValue = paramProperty.getPropValues();
					boolean passEncrypt = paramProperty.propEncrypt();
					String propReqRestart = paramProperty.getPropReqRestart();

					// Check if the property category name is valid
					if (propertyCategoryName == null
							|| !propertyCategoriesNames.contains(propertyCategoryName.trim().toLowerCase())) {

						// Store the list of update failed properties
						updateFailedProps.add(propertyName);

						continue;
					}

					if (passEncrypt) {
						String encryptedText = encryptor.encrypt(propertyValue);
						propertyValue = encryptedText;
					}

					// Update the property in database
					boolean updateStatus = taskDao.updatePropertyValue(propertyCategoryName, propertyName,
							propertyValue);

					LOG.debug("\n====>Updating property[" + propertyName + "] of PropertyCategory:["
							+ propertyCategoryName + "] - Status[" + updateStatus + "]");

					// Check the if the property is updated successfully
					if (updateStatus) {

						// Check if property needs restart
						if (propReqRestart != null && propReqRestart.equalsIgnoreCase("Y"))
							isRestartRequired = "Y";

						Properties dbProperties=null;
						try {
							dbProperties = taskDao.getPropertiesFromDB(propertyCategoryName);
						}catch (Exception e){
							e.printStackTrace();
						}

						System.out.println("isRestartRequired="+isRestartRequired);

						if(!isRestartRequired.equalsIgnoreCase("Y") && dbProperties!=null){
							// Reload the changed properties
							if (propertyCategoryName.equalsIgnoreCase(DatabuckPropertyCategory.appdb.toString())) {
								appDbConnectionProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.resultsdb.toString())) {
								resultDBConnectionProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.cluster.toString())) {
								clusterProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.activedirectory.toString())) {
								activeDirectoryConnectionProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.mongodb.toString())) {
								mongoDbProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.dbdependency.toString())) {
								dbDependencyProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.integration.toString())) {
								integrationProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName.equalsIgnoreCase(DatabuckPropertyCategory.cdp.toString())) {
								cdpProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName.equalsIgnoreCase(DatabuckPropertyCategory.gcp.toString())) {
								gcpProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.azure.toString())) {
								azureProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));

							} else if (propertyCategoryName
									.equalsIgnoreCase(DatabuckPropertyCategory.mapr.toString())) {
								maprProperties.setProperty(propertyName,dbProperties.getProperty(propertyName));
							}
						}else
							System.out.println("UI property update is failed, please restart tomcat");

					} else {
						// Store the list of update failed properties
						updateFailedProps.add(propertyName);
					}
				}

				if (updateFailedProps.isEmpty()) {
					status = "success";
					message = "Properties updated successfully";
				} else {
					message = "Failed to update some properties";

				}

			} else {
				status = "success";
				message = "Properties updated successfully";
			}

		} catch (Exception e) {
			message = "There was a problem";
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("status", status);
		json.put("message", message);
		json.put("updateFailedProps", updateFailedProps);
		json.put("isRestartRequired", isRestartRequired);
		return json;
	}

}
