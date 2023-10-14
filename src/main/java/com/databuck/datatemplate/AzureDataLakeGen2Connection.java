package com.databuck.datatemplate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Service
public class AzureDataLakeGen2Connection {

	@SuppressWarnings("unused")
	public boolean validateConnection(String accountName, String accountKey, String containerName, String folderPath) {
		boolean isConnValid = false;
		try {
			System.out.println("\n=====> Validating Connection...");

			CloudStorageAccount storageAccount = CloudStorageAccount.parse("DefaultEndpointsProtocol=https;AccountName="
					+ accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net");
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			Iterable<ListBlobItem> blobs = container.listBlobs(folderPath);
			for (ListBlobItem blob : blobs) {
			}

			isConnValid = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("connection status: " + isConnValid);
		return isConnValid;

	}

	public List<String> getFolderListFromDataLake(String accountName, String accountKey, String containerName,
			String folderPath) {
		System.out.println("\n====> getFolderListFromDataLake - START <====");

		List<String> foldersList = new ArrayList<String>();
		try {
			folderPath = folderPath.trim().replaceAll("//", "/");
			System.out.println("\n====> folderpath: " + folderPath);

			if (folderPath.endsWith("/"))
				folderPath = folderPath.substring(0, folderPath.length() - 1);

			String parentFolder = (folderPath.endsWith("/")) ? folderPath : folderPath + "/";

			CloudStorageAccount storageAccount = CloudStorageAccount.parse("DefaultEndpointsProtocol=https;AccountName="
					+ accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net");
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			Iterable<ListBlobItem> blobs = container.listBlobs(folderPath);

			System.out.println("\n====> Fetching Folders List.. ");

			for (ListBlobItem blob : blobs) {

				if (blob instanceof CloudBlobDirectory) {
					CloudBlobDirectory directory = (CloudBlobDirectory) blob;

					String directory_name = directory.getPrefix();

					if (parentFolder.equals(directory_name)) {
						Iterable<ListBlobItem> fileBlobs = directory.listBlobs();

						for (ListBlobItem dirBlob : fileBlobs) {
							if (dirBlob instanceof CloudBlobDirectory) {
								String folderName = ((CloudBlobDirectory) dirBlob).getPrefix();
								System.out.println(folderName);

								foldersList.add(folderName);

							}
						}

						break;
					}
				}
			}
			System.out.println("\nfoldersList:\n" + foldersList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return foldersList;

	}

	public List<String> getFilesListInFolderFromDataLake(String accountName, String accountKey, String containerName,
			String folderPath) {
		System.out.println("\n====> getFilesListInFolderFromDataLake - START <====");

		List<String> filesList = new ArrayList<String>();
		try {
			folderPath = folderPath.trim().replaceAll("//", "/");
			System.out.println("\n====> folderpath: " + folderPath);

			if (folderPath.endsWith("/"))
				folderPath = folderPath.substring(0, folderPath.length() - 1);

			String parentFolder = (folderPath.endsWith("/")) ? folderPath : folderPath + "/";

			CloudStorageAccount storageAccount = CloudStorageAccount.parse("DefaultEndpointsProtocol=https;AccountName="
					+ accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net");
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container = blobClient.getContainerReference(containerName);
			Iterable<ListBlobItem> blobs = container.listBlobs(folderPath);

			System.out.println("\n====> Fetching Files List.. ");

			for (ListBlobItem blob : blobs) {

				if (blob instanceof CloudBlobDirectory) {
					CloudBlobDirectory directory = (CloudBlobDirectory) blob;

					String directory_name = directory.getPrefix();
					
					if (parentFolder.equals(directory_name)) {
						Iterable<ListBlobItem> fileBlobs = directory.listBlobs();

						for (ListBlobItem fileBlob : fileBlobs) {

							if (fileBlob instanceof CloudBlob) {
								String folderName = ((CloudBlob) fileBlob).getName();
								System.out.println(folderName);

								filesList.add(folderName);
							}
						}
						
						break;
					}
				}
			}
			System.out.println("\nfilesList:\n" + filesList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filesList;

	}

}
