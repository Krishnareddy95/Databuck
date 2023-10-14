package com.databuck.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.orc.OrcFile;
import org.apache.orc.Reader;
import org.apache.orc.TypeDescription;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.berryworks.edireader.demo.EDItoXML;
import com.databuck.filemonitoring.FileMonitorExternalFile;

import com.opencsv.CSVReader;
import org.apache.log4j.Logger;

@Service
public class DatabuckFileUtility {

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private DatabuckFileUtility databuckFileUtility;
	
	private static final Logger LOG = Logger.getLogger(DatabuckFileUtility.class);

	public String getDemiliterByFormat(String format) {
		String splitBy = "";
		if (format != null) {
			if (format.equalsIgnoreCase("psv")) {
				splitBy = "\\|";
			} else if (format.equalsIgnoreCase("tsv")) {
				splitBy = "\\t";
			} else if (format.equalsIgnoreCase("csv")) {
				splitBy = "\\,";
			}
		}
		return splitBy;
	}

	public Map<String, String> prepareGeneralMetadataFromColumnCount(BufferedReader br, String fileDataFormat) {
		Map<String, String> metadata = new LinkedHashMap<String, String>();
		try {
			String dataLine = br.readLine();
			if (dataLine != null) {

				String splitBy = getDemiliterByFormat(fileDataFormat);
				String[] dataValues = dataLine.split(splitBy);

				for (int i = 1; i <= dataValues.length; i++) {
					String columnName = "col" + i;
					String columnType = "String";
					metadata.put(columnName, columnType);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	public Map<String, String> prepareMetadataFromHeaderValues(BufferedReader br, String headerDataFormat) {
		Map<String, String> metadata = new LinkedHashMap<String, String>();
		try {
			if (br != null) {
				String headerLine = br.readLine();
				if (headerLine != null) {

					String splitBy = getDemiliterByFormat(headerDataFormat);
					String[] headerValues = headerLine.split(splitBy);

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
						metadata.put(columnName, columnType);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	public Map<String, String> prepareFlatFileMetadata(BufferedReader br, String headerDataFormat) {
		Map<String, String> metadata = new LinkedHashMap<String, String>();
		try {
			int lineNumber = 0;
			String line = "";
			String delimiter = getDemiliterByFormat(headerDataFormat);
			while ((line = br.readLine()) != null) {

				if (lineNumber != 0) {
					String[] st = line.split(delimiter);
					String columnName = st[0];
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
				lineNumber++;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	public boolean isPatternMatchedWithRegex(String fileName, String fileNamePattern) {
		boolean matched = false;
		try {
			fileNamePattern = fileNamePattern.replace("\\\\", "\\");
			String regexActivate = appDbConnectionProperties.getProperty("file.pattern.regex");
			LOG.debug("\n===regexActivate ===" + regexActivate);

			if (regexActivate.trim().equalsIgnoreCase("Y") && regexActivate != null) {

				LOG.debug("\n======File Matching with regex between |" + fileNamePattern.trim() + "| and |"
						+ fileName.trim() + "|====>" + Pattern.matches(fileNamePattern.trim(), fileName.trim()));

				matched = Pattern.matches(fileNamePattern.trim(), fileName.trim());

			} else {

				LOG.info("\n======File Matching with Normal Proccess");
				String filePatternTokens[] = fileNamePattern.split("\\*");

				fileName = fileName.trim();

				if (fileNamePattern.equals("*")
						|| (fileNamePattern.startsWith("*") && fileName.endsWith(filePatternTokens[1]))
						|| (fileNamePattern.endsWith("*") && fileName.startsWith(filePatternTokens[0]))
						|| (fileNamePattern.contains("*") && fileName.startsWith(filePatternTokens[0])
								&& fileName.endsWith(filePatternTokens[1]))
						|| (!fileNamePattern.contains("*") && fileName.equals(fileNamePattern))) {

					matched = true;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return matched;
	}

	public boolean isPatternMatched(String fileName, String fileNamePattern) {
		boolean matched = false;
		try {

			String filePatternTokens[] = fileNamePattern.split("\\*");

			fileName = fileName.trim();

			if (fileNamePattern.equals("*")
					|| (fileNamePattern.startsWith("*") && fileName.endsWith(filePatternTokens[1]))
					|| (fileNamePattern.endsWith("*") && fileName.startsWith(filePatternTokens[0]))
					|| (fileNamePattern.contains("*") && fileName.startsWith(filePatternTokens[0])
							&& fileName.endsWith(filePatternTokens[1]))
					|| (!fileNamePattern.contains("*") && fileName.equals(fileNamePattern))) {

				matched = true;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return matched;
	}

	public String getDatabuckHome() {
		String databuckHome = "/opt/databuck";

		if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getenv("DATABUCK_HOME");

		} else if (System.getProperty("DATABUCK_HOME") != null
				&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

			databuckHome = System.getProperty("DATABUCK_HOME");

		}
		LOG.debug("DATABUCK_HOME:" + databuckHome);
		return databuckHome;
	}

	public String downloadAndGetDecryptFilePathForS3(String sourceFilePath) {
		String destinationFilePath = "";
		try {
			LOG.info("\n=====> Decrypting File ..");

			// Get DatabuckHome
			String databuckHome = getDatabuckHome();

			// Get passPhrase
			String decryptedPassPhraseText = appDbConnectionProperties.getProperty("s3.decrypt.passphrase");

			// Get the destinationPath
			Path path = Paths.get(sourceFilePath);
			String fileName = path.getFileName().toString();
			String destinationPath = databuckHome + "/decryptedFiles/";
			destinationFilePath = destinationPath + fileName;

			LOG.debug("\n=====> Source FilePath: " + sourceFilePath);
			LOG.debug("\n=====> Destination FilePath: " + destinationFilePath);

			String scriptLocation = databuckHome + "/scripts/runPgpFileDecrypt.sh";
			LOG.debug("\n**** script location: " + scriptLocation);

			String cmd = scriptLocation + " " + decryptedPassPhraseText + " " + sourceFilePath + " " + destinationPath;
			LOG.debug("\n**** Command : " + cmd);

			List<String> commandList = new ArrayList<String>();
			commandList.add(scriptLocation);
			commandList.add(decryptedPassPhraseText);
			commandList.add(sourceFilePath);
			commandList.add(destinationPath);

			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command(commandList);
			Process process = processBuilder.start();

			/* Process process = Runtime.getRuntime().exec(cmd); */

			BufferedReader oStdOut = null;
			BufferedReader oStdError = null;
			String sOutput = "";
			String sLine = "";

			oStdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			oStdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while ((sLine = oStdOut.readLine()) != null) {
				sOutput = sOutput + ((sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy output : " + sOutput);
			}

			sLine = "";
			sOutput = "";
			while ((sLine = oStdError.readLine()) != null) {
				sOutput = sOutput + ((sLine.isEmpty() || sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy error : " + sOutput);
			}

			while (process.isAlive()) {
				LOG.info("\n=====> Decryption is in progress ..");
				Thread.sleep(2000);
			}

		} catch (Exception e) {
			LOG.error("\n=====> Exception occurred while decrypting File !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return destinationFilePath;
	}

	public void deleteFile(String filePath) {
		try {
			if (filePath != null && !filePath.trim().isEmpty()) {
				File de_file = new File(filePath);
				if (de_file != null && de_file.exists() && de_file.isFile()) {
					de_file.delete();
				}
			} else {
				LOG.info("\n=====> Failed to Delete file: FilePath is blank !!");
			}
		} catch (Exception e) {
			LOG.error("\n=====> Exception occurred while deleting File !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public BufferedReader readFile(String filePath) {
		BufferedReader br = null;
		try {
			if (filePath != null && !filePath.trim().isEmpty()) {
				File dataFile = new File(filePath);
				if (dataFile != null && dataFile.exists() && dataFile.isFile()) {
					br = new BufferedReader(new FileReader(dataFile));
				} else {
					LOG.info("\n====> Failed to read file !!");
				}
			} else {
				LOG.info("\n=====> Failed to read file: FilePath is blank !!");
			}
		} catch (Exception e) {
			LOG.error("\n=====> Exception occurred while deleting File !!");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return br;
	}

	public Map<String, String> getMetadataFromLocalParquetFile(String data_file) {
		data_file = "file:///" + data_file.replace("//", "/");
		LOG.debug("\n====> data_file: " + data_file);

		return getMetadataFromParquetFile(data_file);
	}

	public Map<String, String> getMetadataFromS3IamParquetFile(String data_file) {
		data_file = "s3a://" + data_file.replace("//", "/");
		LOG.debug("\n====> data_file: " + data_file);

		return getMetadataFromParquetFile(data_file);
	}

	public Map<String, String> getMetadataFromParquetFile(String data_file) {

		Map<String, String> metadata = new HashMap<String, String>();
		try {
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(data_file);
			Configuration conf = new Configuration();
			ParquetMetadata readFooter = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER);
			MessageType schema = readFooter.getFileMetaData().getSchema();

			for (Type type : schema.getFields()) {
				metadata.put(type.getName(), type.asPrimitiveType().getPrimitiveTypeName().name());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	public Map<String, String> getMetadataFromLocalORCFile(String data_file) {
		data_file = "file:///" + data_file.replace("//", "/");
		LOG.debug("\n====> data_file: " + data_file);

		return getMetadataFromORCFile(data_file);
	}

	public Map<String, String> getMetadataFromS3IamORCFile(String data_file) {
		data_file = "s3a://" + data_file.replace("//", "/");
		LOG.debug("\n====> data_file: " + data_file);

		return getMetadataFromORCFile(data_file);
	}

	public Map<String, String> getMetadataFromORCFile(String data_file) {

		Map<String, String> metadata = new HashMap<String, String>();
		try {
			Configuration conf = new Configuration();
			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(data_file);

			Reader reader = OrcFile.createReader(path, OrcFile.readerOptions(conf));
			TypeDescription td = reader.getSchema();

			for (String column : td.getFieldNames()) {
				metadata.put(column, "string");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return metadata;
	}

	public String parseExcelORCSVFileReadXMLFileName(String localExternalFilePath, String fileNamePattern,
			String filePatternColumn, String headerColumn, String subFolder) {
		InputStream inp = null;
		CSVReader br = null;
		String xmlFileName = "DUMY";
		Path path = Paths.get(localExternalFilePath);
		String externalfileName = path.getFileName().toString();
		LOG.debug("\n==============  New File Name-->" + fileNamePattern);
		LOG.debug("\n==============  filePatternColumn Index-->" + filePatternColumn);
		LOG.debug("\n==============  headerColumn Index-->" + headerColumn);
		try {
			if (externalfileName.endsWith(".csv")) {
				br = new CSVReader(new FileReader(localExternalFilePath), ',', '"', 1);
				/* br = new BufferedReader(new FileReader(localExternalFilePath)); */
				xmlFileName = parseCSV(br, fileNamePattern, filePatternColumn, headerColumn, subFolder);
			} else {
				inp = new FileInputStream(localExternalFilePath);
				Workbook wb = WorkbookFactory.create(inp);
				xmlFileName = parseExcel(wb.getSheetAt(0), fileNamePattern, filePatternColumn, headerColumn, subFolder);
			}
		} catch (FileNotFoundException ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			LOG.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (externalfileName.endsWith(".csv")) {
					br.close();
				} else {
					inp.close();
				}
			} catch (IOException ex) {
				LOG.error(ex.getMessage());
				ex.printStackTrace();
			}
		}

		return xmlFileName;
	}

	private String parseCSV(CSVReader br, String fileNamePattern, String filePatternColumn, String headerColumn,
			String subFolder) {
		String[] filePathParts = fileNamePattern.split("/");
		String xmlFilName = "NOTFOUND";
		/* String line = ""; */
		String splitBy = ",";
		int ftpFilePatternIndex = Integer.parseInt(filePatternColumn);
		int headerColumnFileIndex = Integer.parseInt(headerColumn);

		String folder = subFolder;
		String fName = fileNamePattern;

		System.out.print("\n============== Matching New File In Mapping CSV sheet");

		if (filePathParts.length > 1) {
			folder = filePathParts[filePathParts.length - 2];
			fName = filePathParts[filePathParts.length - 1];
		}

		LOG.debug("\n========  folderName-->" + folder);
		LOG.debug("\n========  FileName-->" + fName);
		try {
			String[] nextLine;
			while ((nextLine = br.readNext()) != null) {
				if (nextLine != null) {
					if (nextLine[28] != null) {
						String[] localSubFolder = nextLine[14].split("/");
						/*
						 * String[] fileFirstPart =
						 * cellValue[ftpFilePatternIndex].trim().split("\\\\\\\\d");
						 * 
						 * String filePattern = fileFirstPart[0];
						 */

						String filePattern = nextLine[ftpFilePatternIndex].trim();
						/*
						 * System.out.print("\n==========Folder from file--->" +
						 * localSubFolder[localSubFolder.length - 1]);
						 * System.out.print("\n==========file Name from file-->" + filePattern);
						 */

						if (folder.equalsIgnoreCase(localSubFolder[localSubFolder.length - 1])) {

							if (databuckFileUtility.isPatternMatchedWithRegex(fName, filePattern)) {

								xmlFilName = nextLine[headerColumnFileIndex].trim();

							}

						}
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		LOG.debug("\n==================xmlFileName-->" + xmlFilName);

		return xmlFilName;

	}

	private String parseExcel(Sheet sheetAt, String fileNamePattern, String filePatternColumn, String headerColumn,
			String subFolder) {
		Row row = null;

		String[] filePathParts = fileNamePattern.split("/");
		String xmlFilName = "NOTFOUND";
		int ftpFilePatternIndex = Integer.parseInt(filePatternColumn);
		int headerColumnFileIndex = Integer.parseInt(headerColumn);

		String folder = subFolder;
		String fName = fileNamePattern;

		System.out.print("\n============== Matching New File In Mapping Excel sheet");

		if (filePathParts.length > 1) {
			folder = filePathParts[filePathParts.length - 2];
			fName = filePathParts[filePathParts.length - 1];
		}

		LOG.debug("\n========  folderName-->" + folder);
		LOG.debug("\n========  FileName-->" + fName);

		for (int i = 1; i < sheetAt.getLastRowNum(); i++) {
			row = sheetAt.getRow(i);
			if (row.getCell(headerColumnFileIndex) != null) {
				String[] localSubFolder = row.getCell(14).getStringCellValue().split("/");
				/*
				 * String[] fileFirstPart =
				 * row.getCell(ftpFilePatternIndex).getStringCellValue().trim()
				 * .split("\\\\\\\\d");
				 * 
				 * String filePattern = fileFirstPart[0];
				 */

				String filePattern = row.getCell(ftpFilePatternIndex).getStringCellValue().trim();

				/*
				 * System.out.print("\n==========Folder from file--->" +
				 * localSubFolder[localSubFolder.length - 1]);
				 * System.out.print("\n==========file Name from file-->" + filePattern);
				 */

				if (folder.equalsIgnoreCase(localSubFolder[localSubFolder.length - 1])) {
					if (databuckFileUtility.isPatternMatchedWithRegex(fName, filePattern)) {

						xmlFilName = row.getCell(headerColumnFileIndex).getStringCellValue().trim();

					}

				}

			}
		}
		LOG.debug("\n==================xmlFileName-->" + xmlFilName);

		return xmlFilName;
	}

	public String downloadXmlFileToLocal(String xmlFilePath, String xmlFileName) {
		String databuckHome = getDatabuckHome();
		String desatinationPath = databuckHome + "/xmlHeaderFile/";
		// Get the destinationPath
		Path path = Paths.get(xmlFilePath);
		String externalfileName = path.getFileName().toString();

		LOG.debug("\n=====> Downloading xml File-->" + externalfileName);
		/*
		 * String h_awsCliCommand = "aws s3 cp " + xmlFilePath + " "+desatinationPath;
		 */
		/* LOG.debug("\n====> h_awsCliCommand:" + h_awsCliCommand); */

		/*
		 * String scriptLocation = databuckHome + "/scripts/runXmlParse.sh";
		 * LOG.debug("\n**** script location: " + scriptLocation);
		 * 
		 * String cmd = scriptLocation + " " + xmlFilePath + " " + desatinationPath;
		 * LOG.debug("\n**** Command : " + cmd);
		 * 
		 * List<String> commandList = new ArrayList<String>();
		 * commandList.add(scriptLocation); commandList.add(xmlFilePath);
		 * commandList.add(desatinationPath);
		 * 
		 * ProcessBuilder processBuilder = new ProcessBuilder();
		 * processBuilder.command(commandList); Process process;
		 */

		xmlFilePath = "s3://" + xmlFilePath;
		String f_awsCliCommand = "aws s3 cp " + xmlFilePath + " " + desatinationPath;
		LOG.debug("\n====> h_awsCliCommand:" + f_awsCliCommand);

		try {
			BufferedReader oStdOut = null;
			BufferedReader oStdError = null;
			String sOutput = "";
			String sLine = "";

			Process f_child = Runtime.getRuntime().exec(f_awsCliCommand);

			/* Execute program and capture both outputs */
			oStdOut = new BufferedReader(new InputStreamReader(f_child.getInputStream()));
			oStdError = new BufferedReader(new InputStreamReader(f_child.getErrorStream()));

			while ((sLine = oStdOut.readLine()) != null) {
				sOutput = sOutput + ((sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy output : " + sOutput);
			}

			sLine = "";
			sOutput = "";
			while ((sLine = oStdError.readLine()) != null) {
				sOutput = sOutput + ((sLine.isEmpty() || sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy error : " + sOutput);
			}

			while (f_child.isAlive()) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		desatinationPath = desatinationPath + externalfileName;
		LOG.debug("\n=====> Started Converting Xml--> " + externalfileName + " File to CSV");
		String csvFileHeaderPath = convertToCsv(desatinationPath, xmlFileName);
		LOG.debug("\n=====> Converted CSV File Path--> " + csvFileHeaderPath);
		return csvFileHeaderPath;
	}

	private String convertToCsv(String desatinationPath, String xmlFileName) {
		String databuckHome = getDatabuckHome();
		String csvFilePath = databuckHome + "/xmlHeaderFile/";
		csvFilePath = csvFilePath + xmlFileName + ".csv";
		try {

			File f = new File(desatinationPath);

			if (f.exists() && !f.isDirectory()) {

				FileWriter writer;
				//LOG.info("\n=====> Converting..... ");
				writer = new FileWriter(csvFilePath);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();

				// Build Document
				Document document = builder.parse(new File(desatinationPath));

				// Normalize the XML Structure; It's just too important !!
				document.getDocumentElement().normalize();

				// Here comes the root node
				Element root = document.getDocumentElement();
				LOG.debug(root.getNodeName());

				// Get all employees
				NodeList nList = document.getElementsByTagName("column");
				//LOG.debug("============================");
				List<String> columnsList = new ArrayList<String>();
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node node = nList.item(temp);
					//LOG.debug(""); // Just a separator
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						// Print each employee's detail
						Element eElement = (Element) node;
						/*LOG.debug("column : " + eElement.getAttribute("label"));*/
						columnsList.add(eElement.getAttribute("label"));
					}

				}
				String c = String.join(",", columnsList);
				/*LOG.debug("Columns List-->" + c);*/
				writer.write(c);
				writer.close();
				LOG.info("\n------ Convertion End ------");

			} else {
				csvFilePath = "FILENOTFOUND";

			}

		} catch (IOException e) {
			LOG.error("\n=====> Convertion Fail..... ");
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			LOG.error("\n=====> Convertion Fail..... ");
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			LOG.error("\n=====> Convertion Fail..... ");
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			LOG.error("\n=====> Convertion Fail..... ");
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return csvFilePath;
	}

	public Map<String, List<String>> getPatternsListFromExternalFileOld(String externalFilePath,
			String filePatternColumn) {
		InputStream inp = null;
		BufferedReader br = null;
		String line = null;
		String splitBy = ",";
		Map<String, List<String>> patternMap = new HashMap<String, List<String>>();
		int ftpFilePatternIndex = Integer.parseInt(filePatternColumn);

		try {
			LOG.info("\n=====> Fetching patterns from external mapping file ..");

			/*
			 * String f_awsCliCommand = "aws s3 cp " + externalFilePath + " - "; Process
			 * f_child = Runtime.getRuntime().exec(f_awsCliCommand);
			 */
			String localExternalFilePath = downloadExternalFileToLocalFolder(externalFilePath);

			Path path = Paths.get(localExternalFilePath);
			String externalfileName = path.getFileName().toString();
			String filePattern = "";
			String fieldSeparator = "";
			if (externalfileName.endsWith(".csv")) {
				br = new BufferedReader(new FileReader(localExternalFilePath));

				while ((line = br.readLine()) != null) {

					String[] cellValue = line.split(splitBy);

					String[] localSubFolder = cellValue[14].split("/");
					/*
					 * String[] fileFirstPart =
					 * cellValue[ftpFilePatternIndex].trim().split("\\\\\\\\d\\+");
					 * 
					 * String filePattern = fileFirstPart[0]; if (fileFirstPart.length > 1) {
					 * filePattern = filePattern + "*" + fileFirstPart[1]; }
					 */
					filePattern = cellValue[ftpFilePatternIndex].trim();
					fieldSeparator = cellValue[9].trim();

					String subFolderName = localSubFolder[localSubFolder.length - 1].trim().replaceAll("//", "/");

					List<String> patternList = new ArrayList<String>();

					if (patternMap.containsKey(subFolderName) && patternMap.get(subFolderName) != null) {
						patternList = patternMap.get(subFolderName);
					}

					if (filePattern != null && subFolderName != null) {
						patternList.add(filePattern + "=" + fieldSeparator);
						patternMap.put(subFolderName, patternList);
					}

				}

			} else {

				inp = new FileInputStream(localExternalFilePath);
				/* Workbook wb = WorkbookFactory.create(f_child.getInputStream()); */
				Workbook wb = WorkbookFactory.create(inp);

				/* for (int j = 0; j < wb.getNumberOfSheets(); j++) { */
				Sheet sheetAt = wb.getSheetAt(0);
				Row row = null;

				for (int i = 1; i < sheetAt.getLastRowNum(); i++) {
					row = sheetAt.getRow(i);
					String[] localSubFolder = row.getCell(14).getStringCellValue().split("/");
					/*
					 * String[] fileFirstPart =
					 * row.getCell(ftpFilePatternIndex).getStringCellValue().trim()
					 * .split("\\\\\\\\d\\+");
					 * 
					 * String filePattern = fileFirstPart[0]; if (fileFirstPart.length > 1) {
					 * filePattern = filePattern + "*" + fileFirstPart[1]; }
					 */

					filePattern = row.getCell(ftpFilePatternIndex).getStringCellValue().trim();
					fieldSeparator = row.getCell(9).getStringCellValue().trim();

					String subFolderName = localSubFolder[localSubFolder.length - 1].trim().replaceAll("//", "/");
					List<String> patternList = new ArrayList<String>();

					if (patternMap.containsKey(subFolderName) && patternMap.get(subFolderName) != null) {
						patternList = patternMap.get(subFolderName);

					}

					if (filePattern != null && subFolderName != null) {
						patternList.add(filePattern + "=" + fieldSeparator);
						patternMap.put(subFolderName, patternList);
					}
				}
			}
			/* } */
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return patternMap;
	}

	public List<FileMonitorExternalFile> getPatternsListFromExternalFile(String externalFilePath,
			String filePatternColumn) {
		InputStream inp = null;
		CSVReader br = null;
		String splitBy = ",";
		// Map<String, List<String>> patternMap = new HashMap<String, List<String>>();
		List<FileMonitorExternalFile> fileMonitorExternalFile = new ArrayList<>();
		int ftpFilePatternIndex = Integer.parseInt(filePatternColumn);

		try {
			LOG.info("\n=====> Fetching patterns from external mapping file ..");

			/*
			 * String f_awsCliCommand = "aws s3 cp " + externalFilePath + " - "; Process
			 * f_child = Runtime.getRuntime().exec(f_awsCliCommand);
			 */
			String localExternalFilePath = downloadExternalFileToLocalFolder(externalFilePath);

			Path path = Paths.get(localExternalFilePath);
			String externalfileName = path.getFileName().toString();
			String filePattern = "";
			String fieldSeparator = "";
			boolean foundHeader = false;
			if (externalfileName.endsWith(".csv")) {
				/* br = new BufferedReader(new FileReader(localExternalFilePath)); */
				br = new CSVReader(new FileReader(localExternalFilePath), ',', '"', 1);
				int xsltHeader = -1;
				String[] nextLine;
				while ((nextLine = br.readNext()) != null) {
					if (nextLine != null) {

						if (!foundHeader) {

							for (int intInd = 0; intInd < nextLine.length; intInd++) {
								if (nextLine[intInd].equalsIgnoreCase("XSLT_FILE_NAME")) {
									xsltHeader = intInd;
								}

							}
							foundHeader = true;
						}
						FileMonitorExternalFile fmExtFile = new FileMonitorExternalFile();

						String xsltFileName = "";

						String[] localSubFolder = nextLine[14].split("/");
						/*
						 * String[] fileFirstPart =
						 * cellValue[ftpFilePatternIndex].trim().split("\\\\\\\\d\\+");
						 * 
						 * String filePattern = fileFirstPart[0]; if (fileFirstPart.length > 1) {
						 * filePattern = filePattern + "*" + fileFirstPart[1]; }
						 */
						filePattern = nextLine[ftpFilePatternIndex].trim();
						fieldSeparator = nextLine[9].trim();

						String subFolderName = localSubFolder[localSubFolder.length - 1].trim().replaceAll("//", "/");

						if (xsltHeader != -1) {
							xsltFileName = nextLine[xsltHeader].trim();
						}

						/*
						 * List<String> patternList = new ArrayList<String>();
						 * 
						 * if (patternMap.containsKey(subFolderName) && patternMap.get(subFolderName) !=
						 * null) { patternList = patternMap.get(subFolderName); }
						 * 
						 * if (filePattern != null && subFolderName != null) {
						 * patternList.add(filePattern+"="+fieldSeparator);
						 * patternMap.put(subFolderName, patternList); }
						 */
						fmExtFile.setFieldSeparator(fieldSeparator);
						fmExtFile.setFilePattern(filePattern);
						fmExtFile.setSubFolderName(subFolderName);
						fmExtFile.setXsltFileName(xsltFileName);
						fileMonitorExternalFile.add(fmExtFile);

					}

				}

			} else {

				inp = new FileInputStream(localExternalFilePath);
				/* Workbook wb = WorkbookFactory.create(f_child.getInputStream()); */
				Workbook wb = WorkbookFactory.create(inp);

				/* for (int j = 0; j < wb.getNumberOfSheets(); j++) { */
				Sheet sheetAt = wb.getSheetAt(0);
				Row row = null;

				Row headerRow = sheetAt.getRow(0);
				int xsltHeader = -1;

				short minColIx = headerRow.getFirstCellNum();
				short maxColIx = headerRow.getLastCellNum();
				for (short colIx = minColIx; colIx < maxColIx; colIx++) {
					if (headerRow.getCell(colIx).getStringCellValue().trim().equalsIgnoreCase("XSLT_FILE_NAME"))
						;
					{
						xsltHeader = colIx;
					}

				}

				for (int i = 1; i < sheetAt.getLastRowNum(); i++) {
					FileMonitorExternalFile fmExtFile = new FileMonitorExternalFile();
					String xsltFileName = "";
					row = sheetAt.getRow(i);

					String[] localSubFolder = row.getCell(14).getStringCellValue().split("/");
					/*
					 * String[] fileFirstPart =
					 * row.getCell(ftpFilePatternIndex).getStringCellValue().trim()
					 * .split("\\\\\\\\d\\+");
					 * 
					 * String filePattern = fileFirstPart[0]; if (fileFirstPart.length > 1) {
					 * filePattern = filePattern + "*" + fileFirstPart[1]; }
					 */

					filePattern = row.getCell(ftpFilePatternIndex).getStringCellValue().trim();
					fieldSeparator = row.getCell(9).getStringCellValue().trim();

					String subFolderName = localSubFolder[localSubFolder.length - 1].trim().replaceAll("//", "/");

					if (xsltHeader != -1) {
						xsltFileName = row.getCell(xsltHeader).getStringCellValue().trim();
						;
					}

					/*
					 * List<String> patternList = new ArrayList<String>();
					 * 
					 * if (patternMap.containsKey(subFolderName) && patternMap.get(subFolderName) !=
					 * null) { patternList = patternMap.get(subFolderName);
					 * 
					 * }
					 * 
					 * if (filePattern != null && subFolderName != null) {
					 * patternList.add(filePattern+"="+fieldSeparator);
					 * patternMap.put(subFolderName, patternList); }
					 */

					fmExtFile.setFieldSeparator(fieldSeparator);
					fmExtFile.setFilePattern(filePattern);
					fmExtFile.setSubFolderName(subFolderName);
					fmExtFile.setXsltFileName(xsltFileName);
					fileMonitorExternalFile.add(fmExtFile);

				}
			}
			/* } */
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		return fileMonitorExternalFile;
	}

	public String downloadExternalFileToLocalFolder(String externalfileNamePath) {
		String databuckHome = getDatabuckHome();
		String desatinationPath = databuckHome + "/externalFile/";

		LOG.info("\n=====> Downloading External File....");

		// Get the destinationPath
		Path path = Paths.get(externalfileNamePath);
		String externalfileName = path.getFileName().toString();

		LOG.debug("\n====>External File" + externalfileName);
		/*
		 * String h_awsCliCommand = "aws s3 cp " + xmlFilePath + " "+desatinationPath;
		 */

		String f_awsCliCommand = "aws s3 cp " + externalfileNamePath + " " + desatinationPath;

		LOG.debug("\n====> h_awsCliCommand:" + f_awsCliCommand);

		/*
		 * String scriptLocation = databuckHome + "/scripts/runXmlParse.sh";
		 * LOG.debug("\n**** script location: " + scriptLocation);
		 * 
		 * String cmd = scriptLocation + " " + externalfileNamePath + " " +
		 * desatinationPath; LOG.debug("\n**** Command : " + cmd);
		 * 
		 * List<String> commandList = new ArrayList<String>();
		 * commandList.add(scriptLocation); commandList.add(externalfileNamePath);
		 * commandList.add(desatinationPath);
		 * 
		 * ProcessBuilder processBuilder = new ProcessBuilder();
		 * processBuilder.command(commandList); Process process;
		 */

		try {
			BufferedReader oStdOut = null;
			BufferedReader oStdError = null;
			String sOutput = "";
			String sLine = "";

			Process f_child = Runtime.getRuntime().exec(f_awsCliCommand);

			/* Execute program and capture both outputs */
			oStdOut = new BufferedReader(new InputStreamReader(f_child.getInputStream()));
			oStdError = new BufferedReader(new InputStreamReader(f_child.getErrorStream()));

			while ((sLine = oStdOut.readLine()) != null) {
				sOutput = sOutput + ((sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy output : " + sOutput);
			}

			sLine = "";
			sOutput = "";
			while ((sLine = oStdError.readLine()) != null) {
				sOutput = sOutput + ((sLine.isEmpty() || sLine == null) ? "" : sLine) + "\n";
				LOG.debug("copy error : " + sOutput);
			}
			while (f_child.isAlive()) {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
			e.printStackTrace();
		}

		desatinationPath = desatinationPath + externalfileName;
		LOG.debug("\n====>Destination Local Path For ExternalFile---->" + desatinationPath);
		return desatinationPath;
	}

	// Extract EDI Files
	public String ediParserForS3(String sourceFinalName, String sourceFilePath, BufferedReader xsltPathName,
			boolean foundLocal) {

		// Get DatabuckHome
		String databuckHome = getDatabuckHome();
		String destinationPath = databuckHome + "/ediFiles/";
		String fileNameFinal = FilenameUtils.removeExtension(sourceFinalName);
		String xsltFilePath = destinationPath + fileNameFinal + ".xsl";
		String convertedFile = destinationPath + fileNameFinal + ".final";
		String outputFileName = destinationPath + fileNameFinal + ".xml";
		String fileName = "";

		try {
			if (foundLocal) {
				Path path = Paths.get(sourceFilePath);
				fileName = path.getFileName().toString();
			} else {
				fileName = destinationPath + fileNameFinal + ".edi";
				String awsCliCommand = "aws s3 cp " + sourceFilePath + " " + fileName;

				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command(awsCliCommand);
				Process process = processBuilder.start();

				while (process.isAlive()) {
					LOG.info("\n=====> EDI File download is in progress ..");
					Thread.sleep(2000);
				}

			}
			String readXslt;

			// Transform XSLT
			FileWriter fw = new FileWriter(xsltFilePath, true);
			while ((readXslt = xsltPathName.readLine()) != null) {
				readXslt = readXslt.replaceAll("./$", "$");
				fw.write(readXslt);
			}

			fw.close();

			LOG.debug("Read sourcefile :   " + fileName);

			java.io.Reader inputReader = EDItoXML.establishInput(fileName);
			Writer generatedOutput = establishOutput(outputFileName);
			EDItoXML ediToXml = new EDItoXML();
			ediToXml.setInputReader(inputReader);
			ediToXml.setXmlOutputWriter(generatedOutput);
			ediToXml.run();
			LOG.info("\n========> EDI to XML conversion done; Reading stylesheet<=============");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(outputFileName);
			StreamSource stylesource = new StreamSource(xsltFilePath);
			Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesource);
			Source source = new DOMSource(document);
			LOG.info("\n============> Output to csv file <====================");
			Result outputTarget = new StreamResult(new File(convertedFile));
			transformer.transform(source, outputTarget);

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();

		} finally {

			try {
				if (!foundLocal) {
					deleteFile(fileName);
				}
				deleteFile(outputFileName);
				deleteFile(xsltFilePath);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();

			}

		}

		return convertedFile;

	}

	static Writer establishOutput(String outputFileName) {
		Writer generatedOutput;

		try {
			generatedOutput = new OutputStreamWriter(new FileOutputStream(outputFileName), Charset.defaultCharset());
			LOG.debug("\n ========>Output file " + outputFileName + " opened  <==============");
		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		return generatedOutput;
	}

}
