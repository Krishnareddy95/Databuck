package com.databuck.datatemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;

@Service
public class AzureCosmosDb {
	@SuppressWarnings("unused")
	public boolean  validateConnectionCosmos(String uri, String port, String secretKey,String database)throws DocumentClientException {
		boolean isConnValid = true;
		try {
			DocumentClient client = new DocumentClient(uri + ":" + port + "/", secretKey, new ConnectionPolicy(),
					com.microsoft.azure.documentdb.ConsistencyLevel.Session);
			FeedOptions options = new FeedOptions();
			options.setEnableCrossPartitionQuery(true);
			FeedResponse<DocumentCollection> database4 = client.readCollections("/" + "dbs" + "/" + database + "/",
					options);
		
		} catch (Exception e) {
			
			isConnValid=false;
			e.printStackTrace();
		}
		System.out.println("connection status: " + isConnValid);
		return isConnValid;

	}

	public List<String> getFolderListFromCosmosDataLake(String database, String uri, String port, String secretKey) {

		List<String> containerList = new ArrayList<String>();
		try {
			DocumentClient client = new DocumentClient(uri + ":" + port + "/", secretKey, new ConnectionPolicy(),
					com.microsoft.azure.documentdb.ConsistencyLevel.Session);
			FeedOptions options = new FeedOptions();
			options.setEnableCrossPartitionQuery(true);
			FeedResponse<DocumentCollection> database4 = client.readCollections("/" + "dbs" + "/" + database + "/",
					options);
			Iterator<DocumentCollection> item = database4.getQueryIterator();
			while (item.hasNext()) {
				containerList.add(item.next().getId());
			}
			System.out.println("container list...." + containerList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return containerList;

	}
}