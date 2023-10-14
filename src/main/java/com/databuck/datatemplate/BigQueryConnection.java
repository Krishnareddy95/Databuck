package com.databuck.datatemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.Table;

@Service
public class BigQueryConnection {

	public List<String> getListOfTableNamesFromBigQuery(String projectName, String privateKeyId, String privateKey,
			String clientId, String clientEmail, String database) {

		List<String> tableNameData = new ArrayList<String>();
		try {
			BigQuery bigquery = getBigQueryConnection(projectName, privateKeyId, privateKey, clientId, clientEmail,
					database);

			if (bigquery != null) {
				for (Table table : bigquery.listTables(database).getValues()) {
					tableNameData.add(table.getTableId().getTable());
				}
			}
			System.out.println("BigQueryConnection Tables list: " + tableNameData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableNameData;
	}

	public LinkedHashMap<String, String> readTablesFromBigQuery(String projectName, String privateKeyId,
			String privateKey, String clientId, String clientEmail, String datasetName, String tableName) {

		LinkedHashMap<String, String> tableData = new LinkedHashMap<String, String>();
		try {
			BigQuery bigquery = getBigQueryConnection(projectName, privateKeyId, privateKey, clientId, clientEmail,
					datasetName);

			if (bigquery != null) {
				List<Field> fileds = bigquery.getTable(datasetName, tableName).getDefinition().getSchema().getFields();
				for (Field l : fileds) {
					tableData.put(l.getName().toString(), l.getType().getValue().name());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tableData;
	}

	public BigQuery getBigQueryConnection(String projectName, String privateKeyId, String privateKey, String clientId,
			String clientEmail, String database) {
		BigQuery bigquery = null;
		try {
			JSONObject credentialsData = new JSONObject();
			credentialsData.put("type", "service_account");
			credentialsData.put("project_id", projectName);
			credentialsData.put("private_key_id", privateKeyId);
			credentialsData.put("private_key", privateKey);
			credentialsData.put("client_email", clientEmail);
			credentialsData.put("client_id", clientId);
			credentialsData.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
			credentialsData.put("token_uri", "https://oauth2.googleapis.com/token");
			credentialsData.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
			credentialsData.put("client_x509_cert_url",
					"https://www.googleapis.com/robot/v1/metadata/x509/angsumandutta1974%40pricchaa-144923.iam.gserviceaccount.com");

			String filename = projectName + ".json";

			String filePath = System.getenv("DATABUCK_HOME") + "/propertiesFiles";

			String path = filePath + "/" + filename;
			try (FileWriter file = new FileWriter(path)) {

				file.write(credentialsData.toString());
				file.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

			GoogleCredentials credentials;
			File credentialsPath = new File(path);

			try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
				credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
			}
			bigquery = BigQueryOptions.newBuilder().setProjectId(projectName).setCredentials(credentials).build()
					.getService();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bigquery;
	}
}
