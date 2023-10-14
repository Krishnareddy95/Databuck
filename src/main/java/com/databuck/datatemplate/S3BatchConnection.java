package com.databuck.datatemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.databuck.bean.ListDataSchema;
import com.databuck.util.DatabuckFileUtility;
import com.databuck.service.NotificationService;

@Service
public class S3BatchConnection {

	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	@Autowired
	private NotificationService NotificationService;

	public List<String> getListOfTableNamesFromFolder(String accessKey, String secretKey, String bucketName,
			String folderPath, String fileNamePattern, String multiFoldersEnabled) {

		System.out.println("\n====> Start getListOfTableNamesFromFolder For S3 Batch<====");

		System.out.println("\n====> BucketName :" + bucketName);

		folderPath = folderPath.replace("//", "/");

		if (!folderPath.trim().isEmpty() && !folderPath.endsWith("/")) {
			folderPath = folderPath + "/";
		}

		System.out.println("\n====> Folder Path :" + folderPath);

		System.out.println("\n====> FileName Pattern :" + fileNamePattern);
		
		System.out.println("\n====> multiFoldersEnabled :" + multiFoldersEnabled);

		List<String> tableNameList = new ArrayList<String>();

		try {

			if (multiFoldersEnabled != null && multiFoldersEnabled.trim().equalsIgnoreCase("Y")) {

				AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

				ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName)
						.withPrefix(folderPath).withDelimiter("/");

				ListObjectsV2Result listing = s3Client.listObjectsV2(req);

				for (String summary : listing.getCommonPrefixes()) {
					String fileName = summary;
					System.out.println(fileName);

					if (!fileName.equals(folderPath)) {
						fileName = fileName.replace(folderPath, "");
						tableNameList.add(fileName);
					}
				}

				System.out.println("\n====> Folders list in the folderPath [" + folderPath + "] :" + tableNameList);

			} else {

				if (fileNamePattern != null && !fileNamePattern.trim().isEmpty()) {
					AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

					ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName)
							.withPrefix(folderPath).withDelimiter("/");

					ListObjectsV2Result listing = s3Client.listObjectsV2(req);

					for (S3ObjectSummary summary : listing.getObjectSummaries()) {
						String fileName = summary.getKey();
						System.out.println(fileName);

						if (!fileName.equals(folderPath)) {
							fileName = fileName.replace(folderPath, "");

							boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, fileNamePattern);
							if (patternMatched) {
								tableNameList.add(fileName);
							}
						}
					}

					System.out.println("\n====> Files list in the folderPath [" + folderPath + "] :" + tableNameList);

				} else {
					System.out.println("\n====> FileName Pattern cannot be blank !!");
				}
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while retrieving file names list in the folder path !!");
			e.printStackTrace();
		}
		return tableNameList;
	}

	/**
	 * This Method is used to get the metadata of the file from S3 IAMRole Batch
	 * 
	 * @param tableName
	 * @param listDataSchema
	 * @return
	 */
	public List<String> getListOfTableNamesInFolderForS3IAMRole(String bucketName, String folderPath,
			String fileNamePattern, String partitionedFolders, int maxFolderDepth) {

		System.out.println("\n====> Start getListOfTableNamesInFolderForS3IAMRole <====");

		List<String> tableNameList = new ArrayList<String>();

		try {
			System.out.println("\n====> BucketName :" + bucketName);

			folderPath = folderPath.replace("//", "/");

			if (!folderPath.trim().isEmpty() && !folderPath.endsWith("/")) {
				folderPath = folderPath + "/";
			}

			System.out.println("\n====> Folder Path :" + folderPath);

			System.out.println("\n====> FileName Pattern :" + fileNamePattern);

			if (maxFolderDepth > 2) {
				maxFolderDepth = 2;
			}
			System.out.println("\n====> maxFolderDepth :" + maxFolderDepth);

			if (fileNamePattern != null && !fileNamePattern.trim().isEmpty()) {

				String fullPath = (bucketName + "/" + folderPath).replace("//", "/");
				fullPath = "s3://" + fullPath;

				String awsCliCommand = "aws s3 ls " + fullPath;
				System.out.println("\n====> awsCliCommand: " + awsCliCommand);

				Process child = Runtime.getRuntime().exec(awsCliCommand);

				BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
				String line;

				if (partitionedFolders != null && partitionedFolders.equalsIgnoreCase("Y")) {

					List<String> folderList = new ArrayList<String>();

					while ((line = br.readLine()) != null) {

						// Collect the folders
						if (line.endsWith("/")) {
							String[] f_fieldsList = line.split("\\s");

							String folderName = f_fieldsList[3];
							for (int i = 4; i < f_fieldsList.length; ++i) {
								folderName = " " + f_fieldsList[i];
							}
							folderName = folderName.trim();
							// System.out.println("folderName: [" + folderName + "]");

							folderList.add(folderName);
						}
						// collect the files
						else {

							String[] fieldsList = line.split("\\s");

							String fileName = fieldsList[3];
							for (int i = 4; i < fieldsList.length; ++i) {
								fileName = " " + fieldsList[i];
							}
							fileName = fileName.trim();
							// System.out.println("fileName: [" + fileName + "]");

							boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, fileNamePattern);
							if (patternMatched && fileName != "0")
								tableNameList.add(fileName);

						}
					}
					br.close();

					for (int level = 0; level < maxFolderDepth; ++level) {
						List<String> subFolderList = new ArrayList<String>();

						for (String childFolder : folderList) {

							String pfullPath = (bucketName + "/" + folderPath + childFolder).replace("//", "/");
							pfullPath = "s3://" + pfullPath;

							String f_awsCliCommand = "aws s3 ls " + pfullPath;
							System.out.println("f_awsCliCommand: " + f_awsCliCommand);

							Process p_child = Runtime.getRuntime().exec(f_awsCliCommand);

							BufferedReader fbr = new BufferedReader(new InputStreamReader(p_child.getInputStream()));
							String f_line = null;

							while ((f_line = fbr.readLine()) != null) {
								// System.out.println("f_line: " + f_line);

								// Collect the folders
								if (f_line.endsWith("/")) {
									String[] f_fieldsList = f_line.split("\\s");

									String folderName = f_fieldsList[3];
									for (int i = 4; i < f_fieldsList.length; ++i) {
										folderName = " " + f_fieldsList[i];
									}
									folderName = childFolder + folderName.trim();
									// System.out.println("folderName: [" + folderName + "]");

									subFolderList.add(folderName);
								}
								// collect the files
								else {
									String[] fieldsList = f_line.split("\\s");

									String fileName = fieldsList[3];
									for (int i = 4; i < fieldsList.length; ++i) {
										fileName = " " + fieldsList[i];
									}
									fileName = fileName.trim();
									// System.out.println("fileName: [" + fileName + "]");

									boolean patternMatched = databuckFileUtility.isPatternMatched(fileName,
											fileNamePattern);
									if (patternMatched && fileName != "0")
										tableNameList.add(childFolder + fileName);
								}
							}

							fbr.close();
						}
						folderList = subFolderList;

					}

				} else {
					// Get Files list
					while ((line = br.readLine()) != null) {

						if (!line.endsWith("/")) {
							String[] fieldsList = line.split("\\s");

							String fileName = fieldsList[3];
							for (int i = 4; i < fieldsList.length; ++i) {
								fileName = " " + fieldsList[i];
							}
							fileName = fileName.trim();
							// System.out.println("fileName: [" + fileName + "]");

							boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, fileNamePattern);

							if (patternMatched && fileName != "0")
								tableNameList.add(fileName);
						}
					}
					br.close();
				}

				// System.out.println("\n====> Files list in the folderPath [" + folderPath + "]
				// :" + tableNameList);

			} else {
				System.out.println("\n====> FileName Pattern cannot be blank !!");
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while retrieving file names list in the folder path !!");
			e.printStackTrace();
		}

		return tableNameList;
	}

	/**
	 * This Method is used to get the metadata of the file from S3 IAMRole Batch
	 * 
	 * @param tableName
	 * @param listDataSchema
	 * @param childFilePattern
	 * @param subFolder
	 * @param dataFormat
	 * @return
	 */
	public Map<String, String> getMetadataOfTableForS3IAMRole(String tableName, ListDataSchema listDataSchema,
			String childFilePattern, String subFolder, String dataFormat) {
		Map<String, String> metadata = new LinkedHashMap<String, String>();

		try {
			String headerPresent = listDataSchema.getHeaderPresent();
			String folderPath = listDataSchema.getFolderPath();
			String fileDataFormat = listDataSchema.getFileDataFormat();
			String headerPath = listDataSchema.getHeaderFilePath();
			String headerDataFormat = listDataSchema.getHeaderFileDataFormat();
			String headerFileNamePattern = listDataSchema.getHeaderFileNamePattern();
			String bucketName = listDataSchema.getBucketName();
			String fileEncrypted = listDataSchema.getFileEncrypted();
			String externalfileNamePattern = listDataSchema.getExtenalFileNamePattern();
			String fileNamePattern = listDataSchema.getFileNamePattern();

			boolean multiPattern = (listDataSchema.getPartitionedFolders() != null
					&& listDataSchema.getPartitionedFolders().equalsIgnoreCase("Y")
					&& listDataSchema.getMultiPattern() != null
					&& listDataSchema.getMultiPattern().equalsIgnoreCase("Y")) ? true : false;

			System.out.println("\n====> multiPattern: [" + multiPattern + "] subFolder: [" + subFolder
					+ "] childFilePattern: [" + childFilePattern + "]");

			if (multiPattern && headerFileNamePattern.contains("*")) {

				if (childFilePattern != null && !childFilePattern.trim().isEmpty()) {
					headerFileNamePattern = childFilePattern;
				}

				if (subFolder != null && !subFolder.trim().isEmpty()) {
					headerPath = headerPath + "/" + subFolder;
				}
			}

			// handling file with data and header in same file
			if (headerPresent != null && headerPresent.trim().equalsIgnoreCase("Y")) {

				String d_filePath = (bucketName + "/" + folderPath + "/" + tableName).replace("//", "/");

				try {
					BufferedReader dbr = null;
					String decryptedFilePath = "";

					/*
					 * Check if file is encrypted, decrypt it and read
					 */
					if (fileEncrypted != null && fileEncrypted.equalsIgnoreCase("Y")) {
						decryptedFilePath = databuckFileUtility.downloadAndGetDecryptFilePathForS3(d_filePath);
					}

					// For Parquet file
					if (fileDataFormat.equalsIgnoreCase("parquet")) {
						if (fileEncrypted != null && fileEncrypted.equalsIgnoreCase("Y")) {
							metadata = databuckFileUtility.getMetadataFromLocalParquetFile(decryptedFilePath);
						} else {
							metadata = databuckFileUtility.getMetadataFromS3IamParquetFile(d_filePath);
						}
					}
					// For ORC file
					else if (fileDataFormat.equalsIgnoreCase("orc")) {
						if (fileEncrypted != null && fileEncrypted.equalsIgnoreCase("Y")) {
							metadata = databuckFileUtility.getMetadataFromLocalORCFile(decryptedFilePath);
						} else {
							metadata = databuckFileUtility.getMetadataFromS3IamORCFile(d_filePath);
						}
					}
					// For other format files
					else {

						if (fileEncrypted != null && fileEncrypted.equalsIgnoreCase("Y")) {
							dbr = databuckFileUtility.readFile(decryptedFilePath);
						} else {

							d_filePath = "s3://" + d_filePath;

							System.out.println("\n====> d_filePath:" + d_filePath);

							String awsCliCommand = "aws s3 cp " + d_filePath + " - ";
							Process child = Runtime.getRuntime().exec(awsCliCommand);

							System.out.println("\n====> awsCliCommand: " + awsCliCommand);

							dbr = new BufferedReader(new InputStreamReader(child.getInputStream()));

						}

						metadata = databuckFileUtility.prepareMetadataFromHeaderValues(dbr, fileDataFormat);
					}
					if (dbr != null) {
						dbr.close();
					}

					// delete decrypted file, if exists
					databuckFileUtility.deleteFile(decryptedFilePath);

				} catch (Exception e) {
					System.out.println("\n=====> Exception occurred while reading Header File [" + folderPath + "/"
							+ tableName + "] !!");
					e.printStackTrace();
				}

			}
			// if header is null or 'N' then read the header from header path
			else {
				headerPath = headerPath.replace("//", "/");
				if (!headerPath.endsWith("/")) {
					headerPath = headerPath + "/";
				}

				List<String> headerFileList = new ArrayList<String>();

				String fullPath = (bucketName + "/" + headerPath).replace("//", "/");
				fullPath = "s3://" + fullPath;
				String awsCliCommand = "aws s3 ls " + fullPath;
				System.out.println("\n====> awsCliCommand: " + awsCliCommand);

				Process child = Runtime.getRuntime().exec(awsCliCommand);

				BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
				String line;

				// Get Files list
				while ((line = br.readLine()) != null) {

					if (!line.endsWith("/")) {
						String[] fieldsList = line.split("\\s");
						String fileName = fieldsList[3];
						for (int i = 4; i < fieldsList.length; ++i) {
							fileName = " " + fieldsList[i];
						}
						fileName = fileName.trim();
						// System.out.println("fileName: " + fileName);

						boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, headerFileNamePattern);
						if (patternMatched) {
							headerFileList.add(fileName);
						}
					}
				}
				br.close();
				System.out.println("headerFileList: " + headerFileList);

				String headerFileName = "";
				boolean headerFileFound = false;

				String[] fileNameParts = tableName.split("\\.");

				// If only one header file is found for that pattern
				if (headerFileList != null && headerFileList.size() == 1) {
					headerFileName = headerFileList.get(0);
					headerFileFound = true;
				} else {
					for (String fileName : headerFileList) {
						if (fileName.toUpperCase().startsWith(fileNameParts[0].toUpperCase())) {
							headerFileName = fileName;
							headerFileFound = true;
							break;
						}
					}
				}
				if (headerFileFound) {

					String h_filePath = (bucketName + "/" + headerPath + "/" + headerFileName).replace("//", "/");
					h_filePath = "s3://" + h_filePath;

					System.out.println("\n====> h_filePath:" + h_filePath);

					String h_awsCliCommand = "aws s3 cp " + h_filePath + " - ";
					System.out.println("\n====> h_awsCliCommand:" + h_awsCliCommand);

					Process h_child = Runtime.getRuntime().exec(h_awsCliCommand);

					// Read header file
					BufferedReader hbr = new BufferedReader(new InputStreamReader(h_child.getInputStream()));

					// Get Metadata for flat file
					if (fileDataFormat.equalsIgnoreCase("FLAT")) {
						metadata = databuckFileUtility.prepareFlatFileMetadata(hbr, headerDataFormat);
					}
					// Get Metadata for normal file
					else {
						metadata = databuckFileUtility.prepareMetadataFromHeaderValues(hbr, headerDataFormat);
					}

					if (hbr != null) {
						hbr.close();
					}

				} else {
					System.out.println("\n==========>external file is present" + externalfileNamePattern);
					if (externalfileNamePattern != null && externalfileNamePattern.trim().equalsIgnoreCase("Y")) {

						String externalfileName = listDataSchema.getExtenalFileName();
						String filePatternColumn = listDataSchema.getPatternColumn();
						String headerColumn = listDataSchema.getHeaderColumn();
						String localDirectoryColumnIndex = listDataSchema.getLocalDirectoryColumnIndex();
						String decryptedFilePath = "";

						try {
							BufferedReader dbr = null;
							String externalfileNamePath = externalfileName;

							// Download External File Excel From s3 to local server

							String localExternalFilePath = databuckFileUtility
									.downloadExternalFileToLocalFolder(externalfileNamePath);

							// Parse Excel file and read xml file name
							String xmlFileName = databuckFileUtility.parseExcelORCSVFileReadXMLFileName(
									localExternalFilePath, tableName, filePatternColumn, headerColumn, subFolder);

							if (!xmlFileName.equalsIgnoreCase("NOTFOUND")) {

								String xmlFilePath = (bucketName + "/" + headerPath + "/" + xmlFileName + ".xml")
										.replace("//", "/");

								// download xml to local and convert to csv
								String csvFileLocalPath = databuckFileUtility.downloadXmlFileToLocal(xmlFilePath,
										xmlFileName);

								if (!csvFileLocalPath.equalsIgnoreCase("FILENOTFOUND")) {

									System.out.println("File is Present");
									dbr = databuckFileUtility.readFile(csvFileLocalPath);

									metadata = databuckFileUtility.prepareMetadataFromHeaderValues(dbr,
											headerDataFormat);

								} else {

									metadata = processWithDefaultColumns(dataFormat, bucketName, folderPath, tableName,
											dbr, metadata, decryptedFilePath);

									if (metadata != null) {
										String msg = "XML header file is absent under this folder " + xmlFilePath
												+ " and " + tableName
												+ " file is process with with default column Ex:Col1,Col2...etc";
										sendFileMonitorEmailAlertForFailedPatter(msg, tableName);
									}

								}

							} else {

								metadata = processWithDefaultColumns(dataFormat, bucketName, folderPath, tableName, dbr,
										metadata, decryptedFilePath);

								if (metadata != null) {
									String msg = "XML header file name is absent in mapping file for " + tableName
											+ " File and it is process with default column Ex:Col1,Col2...etc";
									sendFileMonitorEmailAlertForFailedPatter(msg, tableName);
								}

							}

							if (dbr != null) {
								dbr.close();
							}
						} catch (Exception e) {
							System.out.println(
									"\n=====> Exception occurred while proccessing External File and XML file !!");
							e.printStackTrace();
						}

					} else {
						System.out.println("\n=====> Header File Not Found !!");

						if (!fileDataFormat.equalsIgnoreCase("FLAT")) {
							System.out.println("\n=====> Preparing general header based on column count ..");

							String d_filePath = (bucketName + "/" + folderPath + "/" + tableName).replace("//", "/");

							try {
								BufferedReader dbr = null;
								String decryptedFilePath = "";

								/*
								 * Check if file is encrypted, decrypt it and read
								 */
								if (fileEncrypted != null && fileEncrypted.equalsIgnoreCase("Y")) {

									decryptedFilePath = databuckFileUtility
											.downloadAndGetDecryptFilePathForS3(d_filePath);

									dbr = databuckFileUtility.readFile(decryptedFilePath);

								} else {

									d_filePath = "s3://" + d_filePath;
									System.out.println("\n====> d_filePath:" + d_filePath);

									String f_awsCliCommand = "aws s3 cp " + d_filePath + " - ";
									Process f_child = Runtime.getRuntime().exec(f_awsCliCommand);

									System.out.println("\n====> f_awsCliCommand: " + f_awsCliCommand);

									dbr = new BufferedReader(new InputStreamReader(f_child.getInputStream()));
								}

								metadata = databuckFileUtility.prepareGeneralMetadataFromColumnCount(dbr,
										fileDataFormat);

								if (dbr != null) {
									dbr.close();
								}

								// delete decrypted file, if exists
								databuckFileUtility.deleteFile(decryptedFilePath);

							} catch (Exception e) {
								System.out.println(
										"\n=====> Exception occurred while reading File [" + d_filePath + "] !!");
								e.printStackTrace();
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;
	}

	private Map<String, String> processWithDefaultColumns(String dataFormat, String bucketName, String folderPath,
			String tableName, BufferedReader dbr, Map<String, String> metadata, String decryptedFilePath) {

		try {
			if (!dataFormat.equalsIgnoreCase("FLAT")) {

				System.out.println("\n=====> Preparing general header based on column count ..");

				String d_filePath = (bucketName + "/" + folderPath + "/" + tableName).replace("//", "/");

				/*
				 * Check if file is encrypted, decrypt it and read
				 */
				if (tableName.endsWith(".pgp") || tableName.endsWith(".PGP") || tableName.endsWith(".gpg")
						|| tableName.endsWith(".GPG")) {

					decryptedFilePath = databuckFileUtility.downloadAndGetDecryptFilePathForS3(d_filePath);

					dbr = databuckFileUtility.readFile(decryptedFilePath);

				} else {

					d_filePath = "s3://" + d_filePath;
					System.out.println("\n====> d_filePath:" + d_filePath);

					String f_awsCliCommand = "aws s3 cp " + d_filePath + " - ";
					Process f_child = Runtime.getRuntime().exec(f_awsCliCommand);

					System.out.println("\n====> f_awsCliCommand: " + f_awsCliCommand);

					dbr = new BufferedReader(new InputStreamReader(f_child.getInputStream()));

				}

				metadata = databuckFileUtility.prepareGeneralMetadataFromColumnCount(dbr, dataFormat);

			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		// delete decrypted file, if exists
		databuckFileUtility.deleteFile(decryptedFilePath);
		return metadata;
	}

	private void sendFileMonitorEmailAlertForFailedPatter(String msg, String tableName) {

		HashMap<String, String> oTokens = new HashMap<>();
		oTokens.put("User", "User");
		oTokens.put("msg", msg);
		oTokens.put("FileName", tableName);
		NotificationService.SendNotification("FILE_MONITORING_PROCESS_FAILED", oTokens, null);

	}

	private String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		System.out.println("DATABUCK_HOME:" + databuckHome);
		return databuckHome;
	}

	public Map<String, String> getMetadataOfTableForS3(ListDataSchema listDataSchema, String tableName) {

		Map<String, String> metadata = new LinkedHashMap<String, String>();

		try {
			String headerPresent = listDataSchema.getHeaderPresent();
			String headerPath = listDataSchema.getHeaderFilePath();
			String headerDataFormat = listDataSchema.getHeaderFileDataFormat();
			String headerFileNamePattern = listDataSchema.getHeaderFileNamePattern();
			String folderPath = listDataSchema.getFolderPath();
			String fileDataFormat = listDataSchema.getFileDataFormat();
			String accessKey = listDataSchema.getAccessKey();
			String secretKey = listDataSchema.getSecretKey();
			String bucketName = listDataSchema.getBucketName();

			AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));

			// handling file with data and header in same file
			if (headerPresent != null && headerPresent.trim().equalsIgnoreCase("Y")) {

				String dataFileFullPath = folderPath + "/" + tableName;
				dataFileFullPath = dataFileFullPath.replace("//", "/");

				try {
					S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, dataFileFullPath));

					BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
					metadata = databuckFileUtility.prepareMetadataFromHeaderValues(br, fileDataFormat);

					if (br != null) {
						br.close();
					}
				} catch (Exception e) {
					System.out.println(
							"\n=====> Exception occurred while reading Data File [" + dataFileFullPath + "] !!");
				}

			}
			// if header is null or 'N' then read the header from header path
			else {

				headerPath = headerPath.replace("//", "/");
				if (!headerPath.endsWith("/")) {
					headerPath = headerPath + "/";
				}

				ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(headerPath)
						.withDelimiter("/");

				ListObjectsV2Result listing = s3Client.listObjectsV2(req);

				List<String> headerFileList = new ArrayList<String>();

				for (S3ObjectSummary summary : listing.getObjectSummaries()) {
					String fileName = summary.getKey();
					fileName = fileName.replace(headerPath, "");
					System.out.println(fileName);

					if (!fileName.equals(headerPath)) {
						// Check if header file matches pattern
						boolean patternMatched = databuckFileUtility.isPatternMatched(fileName, headerFileNamePattern);
						if (patternMatched) {
							headerFileList.add(fileName);
						}
					}
				}

				String headerFileName = "";
				boolean headerFileFound = false;

				String[] fileNameParts = tableName.split("\\.");

				// If only one header file is found for that pattern
				if (headerFileList != null && headerFileList.size() == 1) {
					headerFileName = headerFileList.get(0);
					headerFileFound = true;
				} else {
					for (String fileName : headerFileList) {
						if (fileName.toUpperCase().startsWith(fileNameParts[0].toUpperCase())) {
							headerFileName = fileName;
							headerFileFound = true;
							break;
						}
					}
				}

				if (headerFileFound) {
					String h_filePath = headerPath + headerFileName;
					System.out.println("h_filePath:" + h_filePath);

					S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, h_filePath));

					if (s3object != null) {
						try {
							// Read header file
							BufferedReader br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));

							// Get Metadata for flat file
							if (fileDataFormat.equalsIgnoreCase("FLAT")) {
								metadata = databuckFileUtility.prepareFlatFileMetadata(br, headerDataFormat);
							}
							// Get Metadata for normal file
							else {
								metadata = databuckFileUtility.prepareMetadataFromHeaderValues(br, headerDataFormat);
							}

							if (br != null) {
								br.close();
							}
						} catch (Exception e) {
							System.out.println("\n=====> Exception occurred while reading Header File [" + headerPath
									+ "/" + headerFileName + "] !!");
						}

					} else {
						System.out.println(
								"\n=====> Header File [" + headerPath + "/" + headerFileName + "] is missing !!");
					}
				} else {
					System.out.println("\n=====> Header File Not Found !!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;

	}
}
