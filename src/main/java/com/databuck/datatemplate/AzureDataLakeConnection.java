package com.databuck.datatemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.DirectoryEntry;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.AzureADToken;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

@Service
public class AzureDataLakeConnection {

	public List<String> getListOfFilesFromDataLake(String azureClientId, String azureClientSecret, String azureTenantId,
			String azureServiceURI, String azureFilePath) {
		List<String> fileList = new ArrayList<String>();

		try {
			AccessTokenProvider provider = new ClientCredsTokenProvider(
					"https://login.microsoftonline.com/" + azureTenantId + "/oauth2/token", azureClientId,
					azureClientSecret);
			if (null == azureFilePath) {
				azureFilePath = "/";
			}
			AzureADToken azureADToken;

			azureADToken = provider.getToken();
			String accessToken = azureADToken.accessToken;
			System.out.println("accessToken : " + accessToken);

			ADLStoreClient createClient = ADLStoreClient.createClient(azureServiceURI, provider);

			List<DirectoryEntry> enumerateDirectory = createClient.enumerateDirectory(azureFilePath);

			for (DirectoryEntry directoryEntry : enumerateDirectory) {
				String file = directoryEntry.fullName;
				file = file.replace("/", "").trim();
				fileList.add(file);
				System.out.println("directoryEntry : " + directoryEntry.fullName);

			}
			System.out.println("fileList in AzureDataLakeConnection : " + fileList);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return fileList;
	}

	public Map<String, String> readColumsFromDataLake(String azureClientId, String azureClientSecret,
			String azureTenantId, String azureServiceURI, String azureFilePath, String tableName) {
		Map<String, String> metadata = new LinkedHashMap<String, String>();

		try {
			AccessTokenProvider provider = new ClientCredsTokenProvider(
					"https://login.microsoftonline.com/" + azureTenantId + "/oauth2/token", azureClientId,
					azureClientSecret);

			ADLStoreClient createClient = ADLStoreClient.createClient(azureServiceURI, provider);
			if (null == azureFilePath) {
				azureFilePath = "/";
			}
			String fullFilePath = (azureFilePath + "/" + tableName).replace("//", "/");

			InputStream readStream = createClient.getReadStream(fullFilePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(readStream));
			String line = reader.readLine();
			if (null != line) {
				String splitBy = "\\,";
				String[] headerValues = line.split(splitBy);

				for (int i = 0; i < headerValues.length; i++) {
					String columnName = headerValues[i];
					String columnType = "String";
					columnName = columnName.trim();
					String modifiedColumn = "";
					String[] charArray = columnName.split("(?!^)");
					for (int j = 0; j < charArray.length; j++) {
						if (charArray[j].matches("[' 'a-zA-Z0-9_.+-]")) {

							modifiedColumn = modifiedColumn + charArray[j];
						}
					}

					modifiedColumn = modifiedColumn.replace("-", "_");
					modifiedColumn = modifiedColumn.replace(".", "_");
					modifiedColumn = modifiedColumn.replace(" ", "_");
					metadata.put(modifiedColumn, columnType);
				}
			}

			reader.close();

			System.out.println("metadata : " + metadata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;
	}

}
