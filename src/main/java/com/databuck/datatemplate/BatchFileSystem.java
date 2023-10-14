package com.databuck.datatemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListDataSchema;
import com.databuck.util.DatabuckFileUtility;

@Service
public class BatchFileSystem {

	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	public List<String> getListOfTableNamesFromFolder(String folderPath, String fileNamePattern) {

		System.out.println("\n====> Start getListOfTableNamesFromFolder <====");

		System.out.println("\n====> Folder Path :" + folderPath);

		System.out.println("\n====> FileName Pattern :" + fileNamePattern);

		List<String> tableNameList = new ArrayList<String>();

		try {
			File rootDir = new File(folderPath);

			if (rootDir.isDirectory() && rootDir.exists()) {

				if (fileNamePattern != null && !fileNamePattern.trim().isEmpty()) {

					// Get the list of files based on pattern
					Collection<File> fileList = FileUtils.listFiles(rootDir, new WildcardFileFilter(fileNamePattern),
							null);

					for (File l_file : fileList) {
						String fileName = l_file.getName();
						tableNameList.add(fileName);
					}

					System.out.println("\n====> Files list in the folderPath [" + folderPath + "] :" + tableNameList);

				} else {
					System.out.println("\n====> FileName Pattern cannot be blank !!");
				}
			} else {
				System.out.println("\n====> Folder path :[" + folderPath + "] Directory does not exists !!");
			}
		} catch (Exception e) {
			System.out.println("\n====> Exception occurred while retrieving file names list in the folder path !!");
			e.printStackTrace();
		}
		return tableNameList;
	}

	public Map<String, String> getTablecolumnsFromfilesystem(ListDataSchema listDataSchema, String tableName) {

		Map<String, String> metadata = new LinkedHashMap<String, String>();
		try {
			String headerPresent = listDataSchema.getHeaderPresent();
			String headerFileNamePattern = listDataSchema.getHeaderFileNamePattern();
			String headerPath = listDataSchema.getHeaderFilePath();
			String headerDataFormat = listDataSchema.getHeaderFileDataFormat();
			String fileDataFormat = listDataSchema.getFileDataFormat();

			// handling file with data and header in same file
			if (headerPresent != null && headerPresent.trim().equalsIgnoreCase("Y")) {

				File folderPath = new File(listDataSchema.getFolderPath());

				String dataFileFullPath = folderPath + "/" + tableName;

				// Check if Folder path exists
				if (folderPath != null && folderPath.exists() && folderPath.isDirectory()) {

					File data_file = new File(dataFileFullPath);

					if (data_file != null && data_file.exists() && data_file.isFile()) {
						try {

							if (fileDataFormat.equalsIgnoreCase("parquet")) {
								metadata = databuckFileUtility.getMetadataFromLocalParquetFile(dataFileFullPath);

							} else if (fileDataFormat.equalsIgnoreCase("orc")) {
								metadata = databuckFileUtility.getMetadataFromLocalORCFile(dataFileFullPath);

							} else {
								BufferedReader br = new BufferedReader(new FileReader(data_file));
								metadata = databuckFileUtility.prepareMetadataFromHeaderValues(br, fileDataFormat);

								if (br != null) {
									br.close();
								}
							}
						} catch (Exception e) {
							System.out.println("\n=====> Exception occurred while reading Data File ["
									+ dataFileFullPath + "] !!");
						}

					} else {
						System.out.println("\n=====> Data File [" + dataFileFullPath + "] is missing !!");
					}

				} else {
					System.out.println("\n=====> Data Path is missing !!");
				}
			}
			// if header is null or 'N' then read the header from header path
			else {
				File headerFileDir = new File(headerPath);

				// Check if header path exists
				if (headerFileDir != null && headerFileDir.exists() && headerFileDir.isDirectory()) {

					// Get List of files in header path matching header pattern
					Collection<File> fileList = FileUtils.listFiles(headerFileDir,
							new WildcardFileFilter(headerFileNamePattern), null);

					String headerFileName = "";
					boolean headerFileFound = false;

					// If only one header file is found for that pattern
					if (fileList != null && fileList.size() == 1) {
						headerFileName = fileList.iterator().next().getName();
						headerFileFound = true;
					} else {
						String[] fileNameParts = tableName.split("\\.");

						for (File l_file : fileList) {
							String fileName = l_file.getName();

							if (fileName.toUpperCase().startsWith(fileNameParts[0].toUpperCase())) {
								headerFileName = fileName;
								headerFileFound = true;
								break;
							}
						}
					}

					if (headerFileFound) {
						String headerFileFullPath = headerFileDir + "/" + headerFileName;
						File h_file = new File(headerFileFullPath);

						// Check if header file exists
						if (h_file != null && h_file.exists() && h_file.isFile()) {
							try {
								// Read header file
								BufferedReader br = new BufferedReader(new FileReader(h_file));

								// Get Metadata for flat file
								if (fileDataFormat.equalsIgnoreCase("FLAT")) {
									metadata = databuckFileUtility.prepareFlatFileMetadata(br, headerDataFormat);
								}
								// Get Metadata for normal file
								else {
									metadata = databuckFileUtility.prepareMetadataFromHeaderValues(br,
											headerDataFormat);
								}

								if (br != null) {
									br.close();
								}
							} catch (Exception e) {
								System.out.println("\n=====> Exception occurred while reading Header File ["
										+ headerFileFullPath + "] !!");
							}

						} else {
							System.out.println("\n=====> Header File [" + headerFileFullPath + "] is missing !!");
						}
					} else {
						System.out.println("\n=====> Header File Not Found !!");
					}

				} else {
					System.out.println("\n=====> Header Path is missing !!");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadata;
	}

}
