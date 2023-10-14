package com.databuck.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.databuck.bean.DatabuckProperties;

@Service
public class DatabuckPropertyLoader {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Properties getPropertiesFromDB(String propertyCategory) {
		System.out.println("\n====>Loading properties for the category [" + propertyCategory + "] ...");

		Properties prop = new Properties();
		try {
			List<DatabuckProperties> propertiesList = getPropertiesForCategory(propertyCategory, true);

			if (propertiesList != null) {
				for (DatabuckProperties propertyFields : propertiesList) {
					prop.setProperty(propertyFields.getPropertyName(), propertyFields.getPropertyValue());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}

	public List<DatabuckProperties> getPropertiesForCategory(String propertyCategory, boolean propertyLogEnabled) {
		List<DatabuckProperties> propertiesList = new ArrayList<>();
		try {

			String sql = " select a.*, b.property_category_name from databuck_property_details a, databuck_properties_master b where a.property_category_id = b.property_category_id and  b.property_category_name=?";

			RowMapper<DatabuckProperties> rowMapper = (rs, i) -> {
				String propertyCategoryName = rs.getString("property_category_name");
				String propertyName = rs.getString("property_name");
				String propertyValue = rs.getString("property_value");

				// Check if property is mandatory
				String is_mandatory_field = rs.getString("is_mandatory_field");
				boolean isMandatory = (is_mandatory_field != null && is_mandatory_field.equalsIgnoreCase("Y")) ? true
						: false;

				// Check if property is password
				String is_password_field = rs.getString("is_password_field");
				boolean isPassword = (is_password_field != null && is_password_field.equalsIgnoreCase("Y")) ? true
						: false;

				// Check if property value is encrypted
				String is_value_encrypted = rs.getString("is_value_encrypted");
				boolean isEncrypted = (is_value_encrypted != null && is_value_encrypted.equalsIgnoreCase("Y")) ? true
						: false;

				boolean warning = (isMandatory && (propertyValue == null || propertyValue.isEmpty())) ? true : false;

				// Properties will be printed in console only when propLogEnabled is 'true'
				if (propertyLogEnabled) {
					if (isPassword || isEncrypted)
						System.out.println(
								"Property name:[" + propertyName + "]  value:[Encrypted Data - Do not display]");
					else
						System.out.println("Property name:[" + propertyName + "]  value:[" + propertyValue + "]");
				}

				// If value is encrypted, decrypt and set it
				if (isEncrypted && propertyValue != null && !propertyValue.trim().isEmpty()) {
					try {
						StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
						decryptor.setPassword("4qsE9gaz%!L@UMrK5myY");
						propertyValue = decryptor.decrypt(propertyValue);

					} catch (Exception e) {
						System.out.println("\n====> Exception occurred while decrypting the encrypted property:["
								+ propertyName + "] of Category:[" + propertyCategoryName
								+ "]!!, Please check and update correct value.");
						e.printStackTrace();
					}
				}
				DatabuckProperties propertyFields = new DatabuckProperties();
				propertyFields.setPropertyId(rs.getInt("property_id"));
				propertyFields.setPropertyCategoryId(rs.getInt("property_category_id"));
				propertyFields.setPropertyName(rs.getString("property_name"));
				propertyFields.setPropertyValue(propertyValue);
				propertyFields.setDescription(rs.getString("description"));
				propertyFields.setMandatoryField(isMandatory);
				propertyFields.setPasswordField(isPassword);
				propertyFields.setValueEncrypted(isEncrypted);
				propertyFields.setWarning(warning);
				propertyFields.setPropertyDefaultvalue(rs.getString("property_default_value"));
				propertyFields.setPropertyDataType(rs.getString("property_data_type"));
				propertyFields.setPropRequiresRestart(rs.getString("prop_requires_restart"));
				propertyFields.setLastUpdatedAt(rs.getTimestamp("last_updated_at"));
				return propertyFields;
			};

			propertiesList = jdbcTemplate.query(sql, rowMapper, propertyCategory);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return propertiesList;
	}

}
