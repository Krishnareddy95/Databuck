package com.databuck.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.databuck.datatemplate.MYSQLConnection;
import com.databuck.service.RemoteClusterAPIService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.DateAnalysisData;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.NumericalAnalysisData;
import com.databuck.bean.StringAnalysisData;
import com.databuck.bean.SubsegmentSelector;
import com.databuck.constants.DatabuckConstants;
import com.databuck.controller.DataTemplateController;
import com.databuck.dao.IDataTemplateAddNewDAO;
import com.databuck.dao.IProjectDAO;
import com.databuck.dao.ITaskDAO;
import com.databuck.datatemplate.AmazonRedshiftConnection;
import com.databuck.datatemplate.AzureConnection;
import com.databuck.datatemplate.AzureDataLakeConnection;
import com.databuck.datatemplate.BatchFileSystem;
import com.databuck.datatemplate.BigQueryConnection;
import com.databuck.datatemplate.CassandraConnection;
import com.databuck.datatemplate.HiveConnection;
import com.databuck.datatemplate.MSSQLConnection;
import com.databuck.datatemplate.MsSqlActiveDirectoryConnection;
import com.databuck.datatemplate.OracleConnection;
import com.databuck.datatemplate.OracleRACConnection;
import com.databuck.datatemplate.PostgresConnection;
import com.databuck.datatemplate.S3BatchConnection;
import com.databuck.datatemplate.SnowflakeConnection;
import com.databuck.datatemplate.TeradataConnection;
import com.databuck.datatemplate.VerticaConnection;
import com.databuck.security.LogonManager;
import com.databuck.service.IDataAlgorithService;
import com.databuck.util.DatabuckFileUtility;
import com.databuck.util.DateUtility;
import com.databuck.util.MathsUtility;
import com.databuck.util.StringUtility;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
@Service
public class DataAlgorithServiceImpl implements IDataAlgorithService {

	@Autowired
	IProjectDAO projectDao;
	
	@Autowired
	DataTemplateController DataTemplateController;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	IDataTemplateAddNewDAO DataTemplateAddNewDAO;

	@Autowired
	MsSqlActiveDirectoryConnection msSqlActiveDirectoryConnectionObject;

	@Autowired
	VerticaConnection verticaconnection;

	@Autowired
	OracleConnection oracleconnection;

	@Autowired
	PostgresConnection postgresConnection;

	@Autowired
	TeradataConnection teradataConnection;

	@Autowired
	OracleRACConnection OracleRACConnection;

	@Autowired
	CassandraConnection cassandraconnection;

	@Autowired
	HiveConnection hiveconnection;

	@Autowired
	AmazonRedshiftConnection amazonRedshiftConnection;

	@Autowired
	MSSQLConnection mSSQLConnection;

	@Autowired
	BatchFileSystem batchFilesystem;

	@Autowired
	SnowflakeConnection snowFlakeConnection;

	@Autowired
	BigQueryConnection bigQueryConnection;

	@Autowired
	S3BatchConnection s3BatchConnection;

	@Autowired
	AzureDataLakeConnection azureDataLakeConnection;

	@Autowired
	AzureConnection azureConnection;

	@Autowired
	private MYSQLConnection mysqlConnection;
	
	@Autowired
	private LogonManager logonManager;
	
	@Autowired
	private ITaskDAO iTaskDAO;
	
	@Autowired
	private DatabuckFileUtility databuckFileUtility;

	@Autowired
	private RemoteClusterAPIService remoteClusterAPIService;
	
	private static final Logger LOG = Logger.getLogger(DataAlgorithServiceImpl.class);

	public Map generateAnalysisData(ResultSet resultSetFromDb, List<String> primaryKeys, HttpSession session,
			String selTableName) throws Exception {
		Map<String, NumericalAnalysisData> numericalData = new HashMap<String, NumericalAnalysisData>();
		Map<String, StringAnalysisData> stringData = new HashMap<String, StringAnalysisData>();
		Map<String, DateAnalysisData> dateData = new HashMap<String, DateAnalysisData>();

		List<String> stringColumns = new ArrayList<String>();
		List<String> numericalColumns = new ArrayList<String>();
		List<String> dateColumns = new ArrayList<String>();
		List<Integer> dateColumnsNumber = new ArrayList<Integer>();

		Map<String, String> finalMap = new HashMap<String, String>();
		Map<String, List<String>> dataRuleMap = new HashMap<String, List<String>>();
		List<String> checkForDateInInteger = new ArrayList<String>();

		LOG.info("Inside generateAnalysisData method");

		ResultSetMetaData metaData = resultSetFromDb.getMetaData();
		// ResultSet resultSetFromDb2 = resultSetFromDb;
		List<String> numericDataTypesList = new ArrayList<String>();
		numericDataTypesList.add("number");
		numericDataTypesList.add("integer");
		numericDataTypesList.add("numeric");
		numericDataTypesList.add("float");
		numericDataTypesList.add("double");
		numericDataTypesList.add("integer");
		numericDataTypesList.add("int");
		numericDataTypesList.add("bigint");
		numericDataTypesList.add("smallint");
		numericDataTypesList.add("decimal");
		numericDataTypesList.add("money");
		numericDataTypesList.add("int4");
		List<String> stringDataTypesList = new ArrayList<String>();
		stringDataTypesList.add("varchar2");
		stringDataTypesList.add("varchar");
		stringDataTypesList.add("char");
		stringDataTypesList.add("text");
		stringDataTypesList.add("string");
		stringDataTypesList.add("nvarchar");
		stringDataTypesList.add("nchar");

		try {
			// Based on metadata, identify string, date and numerical columns.
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				if (columnName.contains(".")) {
					String[] split = columnName.split("\\.");
					columnName = split[1];
				}
				String columnType = metaData.getColumnTypeName(i);
				if (numericDataTypesList.contains(columnType.toLowerCase())) {
					numericalColumns.add(columnName);
				} else if (stringDataTypesList.contains(columnType.toLowerCase())) {
					stringColumns.add(columnName);
				} else if (columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("datetime")) {
					dateColumns.add(columnName);

				}
			}

			LOG.debug("Numerical Columns size:" + numericalColumns.size());
			LOG.debug("String Columns size:" + stringColumns.size());
			LOG.debug("Date Columns size:" + dateColumns.size());

		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw new Exception("Problem reading table metadata..");
		}

		// Generate 3 separate maps for numerical, string and date data
		// so that analsis can be performed on them.
		Map<String, List<Double>> numericalDataMap = new HashMap<String, List<Double>>();
		Map<String, List<String>> stringDataMap = new HashMap<String, List<String>>();
		Map<String, List<Date>> dateDataMap = new HashMap<String, List<Date>>();

		try {
			// ResultSet resultSetFromDb2 = resultSetFromDb;
			int maxrows = 0;

			while (resultSetFromDb.next()) {

				maxrows++;

				List<Double> numData;
				List<String> strData;
				List<Date> dtData;

				for (Iterator<String> numIterator = numericalColumns.iterator(); numIterator.hasNext();) {
					String columnName = (String) numIterator.next();
					double data = resultSetFromDb.getDouble(columnName);

					int dataSize = (int) Math.round(data);

					if (String.valueOf(dataSize).length() == 8) {
						checkForDateInInteger.add(columnName);

					}

					if (numericalDataMap.get(columnName) == null) {
						numData = new ArrayList<Double>();
						numData.add(data);
						numericalDataMap.put(columnName, numData);
					} else {
						numericalDataMap.get(columnName).add(data);
					}

				}

				for (Iterator<String> strIterator = stringColumns.iterator(); strIterator.hasNext();) {
					String columnName = (String) strIterator.next();
					String data = resultSetFromDb.getString(columnName);
					if (data != null) {
						if (stringDataMap.get(columnName) == null) {
							strData = new ArrayList<String>();
							strData.add(data);
							stringDataMap.put(columnName, strData);
						} else {
							stringDataMap.get(columnName).add(data);
						}
					}
				}

				for (Iterator<String> dtIterator = dateColumns.iterator(); dtIterator.hasNext();) {
					String columnName = (String) dtIterator.next();
					Date data;
					try {
						data = resultSetFromDb.getDate(columnName);
					} catch (Exception e) {
						LOG.error(e.getMessage());
						data = null;
					}

					if (data == null) {
						try {
							data = resultSetFromDb.getTime(columnName);
						} catch (Exception e) {
							LOG.error(e.getMessage());
							data = null;
						}

					}
					if (data != null) {
						if (dateDataMap.get(columnName) == null) {
							dtData = new ArrayList<Date>();
							dtData.add(data);
							dateDataMap.put(columnName, dtData);
						} else {
							dateDataMap.get(columnName).add(data);
						}
					}
				}

			}

			LOG.info("Numerical data map created");

			// ResultSetMetaData metaData = resultSetFromDb.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				String columnType = metaData.getColumnTypeName(i);
				if (numericDataTypesList.contains(columnType.toLowerCase())) {
					if (checkForDateInInteger.contains(columnName)) {
						dateColumnsNumber.add(i);
					}

				} else if (stringDataTypesList.contains(columnType.toLowerCase())) {
					stringColumns.add(columnName);
				} else if (columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("datetime")) {
					dateColumns.add(columnName);
					dateColumnsNumber.add(i);
				}
			}
			// ListApplications listApplications = new ListApplications();
			/*
			 * if(appDbConnectionProperties.getProperty("dateRuleCheck") != null &&
			 * appDbConnectionProperties.getProperty("dateRuleCheck").trim().
			 * equalsIgnoreCase("Y")){ dataRuleMap =
			 * PatternMatchUtility.dateRulePattern(dateColumnsNumber, resultSetFromDb1,
			 * maxrows+1, session); }
			 */

		} catch (SQLException e) {
			LOG.error(e.getMessage());
			throw new Exception("Problem reading table data..");
		}

		String htmlData = analyseNumericalData(numericalDataMap, numericalData);
		finalMap.put("Numerical", htmlData);

		htmlData = analyseStringData(stringDataMap, stringData);
		finalMap.put("String & Character", htmlData);

		htmlData = analyseDateData(dateDataMap, dateData);
		finalMap.put("Date", htmlData);

		htmlData = DataTemplateController.generateDataDefinitionData(resultSetFromDb, primaryKeys, session,
				numericalData, stringData, dateData, selTableName, stringDataMap);
		finalMap.put("dataDefinition", htmlData);
		return finalMap;
	}

	private Map<String, String> getParquetMetadata(String folder, String hostURI) throws Exception {
		return databuckFileUtility.getMetadataFromParquetFile(hostURI+"/"+folder);
	}

	public InputStream getTableDataFromFileSystemHDFS(MultipartFile file) {
		// File sFile = new File(file);
		InputStream fis = null;

		try {
			fis = file.getInputStream();

		} catch (IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return fis;

	}

	public InputStream getTableDataFromS3(String bucketName, String key, String accessId, String secretKey) {

		AWSCredentials credentials = new BasicAWSCredentials(accessId, secretKey);
		S3Object s3object = null;

		// create a client connection based on credentials
		AmazonS3 s3client = new AmazonS3Client(credentials);
		/*
		 * @SuppressWarnings("deprecation") AmazonS3 s3Client = new AmazonS3Client(new
		 * ProfileCredentialsProvider());
		 */
		try {
			LOG.info("Downloading an object");
			s3object = s3client.getObject(new GetObjectRequest(bucketName, key));
			LOG.debug("Content-Type: " + s3object.getObjectMetadata().getContentType());
			// displayTextInputStream(s3object.getObjectContent());

		} catch (AmazonServiceException ase) {
			LOG.error("Caught an AmazonServiceException, which" + " means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			LOG.debug("Error Message:    " + ase.getMessage());
			LOG.debug("HTTP Status Code: " + ase.getStatusCode());
			LOG.debug("AWS Error Code:   " + ase.getErrorCode());
			LOG.debug("Error Type:       " + ase.getErrorType());
			LOG.debug("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			LOG.error("Caught an AmazonClientException, which means" + " the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			LOG.error("Error Message: " + ace.getMessage());
		}
		return s3object.getObjectContent();

	}

	public Map populateTableAnalysisDataForS3(HttpSession session, String dataLocation,
			Map<String, String> columnMetaData, MultipartFile file, String hostURI, String folder, String userLogin,
			String password, String dataFormat) {

		Map<String, NumericalAnalysisData> numericalData = new HashMap<String, NumericalAnalysisData>();
		Map<String, StringAnalysisData> stringData = new HashMap<String, StringAnalysisData>();
		Map<String, DateAnalysisData> dateData = new HashMap<String, DateAnalysisData>();

		List<String> stringColumns = new ArrayList<String>();
		List<String> numericalColumns = new ArrayList<String>();
		List<String> dateColumns = new ArrayList<String>();

		Map<String, String> finalMap = new HashMap<String, String>();

		try {
			List<String> numericDataTypesList = new ArrayList<String>();
			numericDataTypesList.add("number");
			numericDataTypesList.add("integer");
			numericDataTypesList.add("numeric");
			numericDataTypesList.add("float");
			numericDataTypesList.add("double");
			numericDataTypesList.add("integer");
			numericDataTypesList.add("int");
			numericDataTypesList.add("bigint");
			numericDataTypesList.add("smallint");
			numericDataTypesList.add("decimal");
			numericDataTypesList.add("money");
			numericDataTypesList.add("int4");
			numericDataTypesList.add("long");
			List<String> stringDataTypesList = new ArrayList<String>();
			stringDataTypesList.add("varchar2");
			stringDataTypesList.add("varchar");
			stringDataTypesList.add("char");
			stringDataTypesList.add("text");
			stringDataTypesList.add("string");
			stringDataTypesList.add("nvarchar");
			stringDataTypesList.add("nchar");
			// Based on metadata, identify string, date and numerical columns.

			for (Entry<String, String> e : columnMetaData.entrySet()) {
				String columnName = e.getKey();
				String columnType = e.getValue();
				if (numericDataTypesList.contains(columnType.toLowerCase())) {
					numericalColumns.add(columnName);
				} else if (stringDataTypesList.contains(columnType.toLowerCase())) {
					stringColumns.add(columnName);
				} else if (columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("datetime")) {
					dateColumns.add(columnName);
				}
			}
			LOG.debug("numericalColumns=" + numericalColumns);
			LOG.debug("stringColumns=" + stringColumns);
			LOG.debug("dateColumns=" + dateColumns);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		Map<String, List<Double>> numericalDataMap = new HashMap<String, List<Double>>();
		Map<String, List<String>> stringDataMap = new HashMap<String, List<String>>();
		Map<String, List<Date>> dateDataMap = new HashMap<String, List<Date>>();
		InputStream input = null;
		if (dataLocation.trim().equalsIgnoreCase("S3")) {
			input = getTableDataFromS3(hostURI, folder, userLogin, password);
		} else if (dataLocation.trim().equalsIgnoreCase("file system")
				|| dataLocation.trim().equalsIgnoreCase("HDFS")) {
			input = getTableDataFromFileSystemHDFS(file);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		long counter = 1l;
		String line = null;

		List<Double> numData;
		List<String> strData;
		List<Date> dtData;

		try {
			line = reader.readLine();
			List<String> datatypes = new ArrayList<String>();
			datatypes.add("int");
			datatypes.add("char");
			datatypes.add("long");
			datatypes.add("float");
			datatypes.add("double");
			datatypes.add("varchar");
			datatypes.add("text");
			datatypes.add("string");
			datatypes.add("date");
			datatypes.add("number");
			datatypes.add("integer");
			datatypes.add("numeric");
			datatypes.add("double");
			datatypes.add("bigint");
			datatypes.add("smallint");
			datatypes.add("decimal");
			datatypes.add("money");
			datatypes.add("int4");
			datatypes.add("varchar2");
			datatypes.add("char");
			datatypes.add("string");
			datatypes.add("nvarchar");
			datatypes.add("nchar");

			ArrayList<String> colName = new ArrayList<String>();
			if (dataFormat.equalsIgnoreCase("psv") || dataFormat.equalsIgnoreCase("tsv")) {
				String splitBy = "\\|";
				if (dataFormat.equalsIgnoreCase("psv")) {
					splitBy = "\\|";
				} else if (dataFormat.equalsIgnoreCase("tsv")) {
					splitBy = "\t";
				}
				for (String word : line.split(splitBy)) {
					String columnName = word.trim();
					String newColumn = null;

					int first = word.lastIndexOf("(");
					int last = word.lastIndexOf(")");
					if (first != -1 && last != -1) {
						if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
							columnName = word.substring(0, first);
						} else {
							columnName = word.substring(0, last);
						}
					} else {
						columnName = word;
					}

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

					LOG.debug("columnName=" + modifiedColumn);
					colName.add(modifiedColumn);
				}
			} else {
				for (String word : line.split("\\,")) {
					String columnName = word;
					String columnType = "String";

					String newColumn = null;

					int first = word.lastIndexOf("(");
					int last = word.lastIndexOf(")");
					if (first != -1 && last != -1) {
						if (datatypes.contains(word.substring(first + 1, last).trim().toLowerCase())) {
							columnType = word.substring(first + 1, last).trim();
							columnName = word.substring(0, first);
						} else {
							columnName = word.substring(0, last);
						}
					} else {
						columnName = word;
					}
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

					LOG.debug("columnName=" + modifiedColumn);
					LOG.debug("columnType=" + columnType);
					colName.add(modifiedColumn);
				}
			}

			String[] colName1 = colName.toArray(new String[colName.size()]);
			line = reader.readLine();
			while ((counter <= 10000) && (line != null)) {
				String[] cn = line.split(",");
				for (int i = 0; i < line.split(",").length; i++) {
					LOG.info("ColumnArray" + colName1[i]);
					if (numericalColumns.contains(colName1[i])) {
						if (numericalDataMap.get(colName1[i]) == null) {
							numData = new ArrayList<Double>();
							numData.add(Double.parseDouble(cn[i]));
							numericalDataMap.put(colName1[i], numData);
						} else {
							numericalDataMap.get(colName1[i]).add(Double.parseDouble(cn[i]));
						}
					}
					if (stringColumns.contains(colName1[i])) {
						if (stringDataMap.get(colName1[i]) == null) {
							strData = new ArrayList<String>();
							strData.add(cn[i]);
							stringDataMap.put(colName1[i], strData);
						} else {
							stringDataMap.get(colName1[i]).add(cn[i]);
						}
					}

					SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
					if (dateColumns.contains(colName1[i])) {
						if (dateDataMap.get(colName1[i]) == null) {
							dtData = new ArrayList<Date>();
							dtData.add(dt.parse(cn[i]));
							dateDataMap.put(colName1[i], dtData);
						} else {
							dateDataMap.get(colName1[i]).add(dt.parse(cn[i]));
						}
					}
				}
				counter++;
				line = reader.readLine();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOG.error(e1.getMessage());
			e1.printStackTrace();
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ex) {
				LOG.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		String htmlData = analyseNumericalData(numericalDataMap, numericalData);
		finalMap.put("Numerical", htmlData);

		htmlData = analyseStringData(stringDataMap, stringData);
		finalMap.put("String & Character", htmlData);

		htmlData = analyseDateData(dateDataMap, dateData);
		finalMap.put("Date", htmlData);

		try {
			htmlData = DataTemplateController.generateDataDefinitionDataForS3(columnMetaData, session, numericalData,
					stringData, dateData, folder);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		finalMap.put("dataDefinition", htmlData);
		LOG.debug("finalMap=" + finalMap);
		return finalMap;
	}

	public Map populateTableAnalysisDataForParquet(ModelAndView model, HttpSession session, HttpServletRequest request,
			HttpServletResponse response, String dataLocation, String hostURI, String folder, String userLogin,
			String password, String dataFormat) throws Exception {
		boolean flag = false;
		Map<String, NumericalAnalysisData> numericalData = new HashMap<String, NumericalAnalysisData>();
		Map<String, StringAnalysisData> stringData = new HashMap<String, StringAnalysisData>();
		Map<String, DateAnalysisData> dateData = new HashMap<String, DateAnalysisData>();

		List<String> stringColumns = new ArrayList<String>();
		List<String> numericalColumns = new ArrayList<String>();
		List<String> dateColumns = new ArrayList<String>();

		Map<String, String> finalMap = new HashMap<String, String>();
		Map<String, String> columnMetaData = null;
		try {
			List<String> numericDataTypesList = new ArrayList<String>();
			numericDataTypesList.add("INT32");

			numericDataTypesList.add("number");
			numericDataTypesList.add("integer");
			numericDataTypesList.add("numeric");
			numericDataTypesList.add("float");
			numericDataTypesList.add("double");
			numericDataTypesList.add("integer");
			numericDataTypesList.add("int");
			numericDataTypesList.add("bigint");
			numericDataTypesList.add("smallint");
			numericDataTypesList.add("decimal");
			numericDataTypesList.add("money");
			numericDataTypesList.add("int4");
			numericDataTypesList.add("long");
			List<String> stringDataTypesList = new ArrayList<String>();
			stringDataTypesList.add("binary");
			stringDataTypesList.add("varchar2");
			stringDataTypesList.add("varchar");
			stringDataTypesList.add("char");
			stringDataTypesList.add("text");
			stringDataTypesList.add("string");
			stringDataTypesList.add("nvarchar");
			stringDataTypesList.add("nchar");
			// Based on metadata, identify string, date and numerical columns.

			columnMetaData = getParquetMetadata(folder, hostURI);

			for (Entry<String, String> e : columnMetaData.entrySet()) {
				String columnName = e.getKey();
				String columnType = e.getValue();
				// LOG.debug(columnName+":"+columnType);
				if (columnType.equals("INT32")) {
					columnType = "int";
				}
				if (numericDataTypesList.contains(columnType.toLowerCase())) {
					numericalColumns.add(columnName.trim());
				} else if (stringDataTypesList.contains(columnType.toLowerCase())) {
					stringColumns.add(columnName.trim());
				} else if (columnType.equalsIgnoreCase("INT96") || columnType.equalsIgnoreCase("DATE")
						|| columnType.equalsIgnoreCase("datetime")) {
					dateColumns.add(columnName.trim());
				}
			}
			if (stringColumns.size() > 0 && numericalColumns.size() < 1 && dateColumns.size() < 1) {
				columnMetaData.clear();
				numericalColumns.clear();
				stringColumns.clear();
				dateColumns.clear();
				finalMap.put("isRawData", "Y");
				flag = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}

		String htmlData = "";
		try {
			htmlData = DataTemplateController.generateDataDefinitionDataForS3(columnMetaData, session, numericalData,
					stringData, dateData, folder);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finalMap.put("dataDefinition", htmlData);
		return finalMap;
	}

	public String analyseNumericalData(Map<String, List<Double>> numericalDataMap,
			Map<String, NumericalAnalysisData> numericalData) {
		String htmlData = "";
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);

			if (numericalDataMap.size() > 0) {
				for (Iterator<String> iter = numericalDataMap.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					List<Double> numberList = numericalDataMap.get(key);
					Map<Double, Integer> numberMap = new HashMap<Double, Integer>();
					ArrayList<String> numberListInStringForm = new ArrayList<String>();
					for (Iterator<Double> numIterator = numberList.iterator(); numIterator.hasNext();) {
						Double singleNumber = numIterator.next();
						Integer value = 1;

						int fieldValue = (int) (double) singleNumber;

						numberListInStringForm.add(String.valueOf(fieldValue));
						if (numberMap.containsKey(singleNumber)) {
							value = numberMap.get(singleNumber);
							value++;
						}
						numberMap.put(singleNumber, value);
					}
					NumericalAnalysisData numAnalysisData = new NumericalAnalysisData();
					double max = Collections.max(numberList);
					double min = Collections.min(numberList);

					/*
					 * ArrayList<String> numberListInStringForm = new ArrayList<String>(); Iterator
					 * it = numberList.iterator(); while (it.hasNext()) { int x = (int) it.next();
					 * double val = (double) x; int fieldValue = (int) (double) it.next();
					 *
					 * numberListInStringForm.add(String.valueOf(fieldValue)); }
					 */

					double maxLength = StringUtility.calculateMaxLength(numberListInStringForm);
					double minLength = StringUtility.calculateMinLength(numberListInStringForm);

					double average = MathsUtility.calculateAverage(numberList);
					double median = MathsUtility.calculateMedian(numberList);
					double percentOfNullValues = MathsUtility.calculatePercentOfNullValues(numberList);
					double percentOfUniqueValues = MathsUtility.calculatePercentOfUniqueValues(numberList);

					numAnalysisData.setKey(key);
					numAnalysisData.setSampleData(new Double(numericalDataMap.get(key).get(0)).toString());

					numAnalysisData.setMinLength(decimalFormat.format(minLength));
					numAnalysisData.setMaxLength(decimalFormat.format(maxLength));
					numAnalysisData.setMin(decimalFormat.format(min));
					numAnalysisData.setMax(decimalFormat.format(max));
					numAnalysisData.setAverage(decimalFormat.format(average));
					numAnalysisData.setMedian(decimalFormat.format(median));
					numAnalysisData.setPercentOfNullValues(decimalFormat.format(percentOfNullValues));
					numAnalysisData.setPercentOfUniqueValues(decimalFormat.format(percentOfUniqueValues));
					numAnalysisData.setDataMap(numberMap);
					numericalData.put(key, numAnalysisData);
				}

				Gson gson = new Gson();
				htmlData = gson.toJson(numericalData);
				LOG.debug("Numerical data analysis:" + htmlData);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return htmlData;
	}

	public String analyseStringData(Map<String, List<String>> stringDataMap,
			Map<String, StringAnalysisData> stringData) {
		String htmlData = new String("");
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);

			if (stringDataMap.size() > 0) {
				for (Iterator<String> iterator = stringDataMap.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					List<String> strList = stringDataMap.get(key);
					Map<String, Integer> stringMap = new HashMap<String, Integer>();
					for (Iterator<String> strIterator = strList.iterator(); strIterator.hasNext();) {
						String singleStr = strIterator.next();
						Integer value = 1;
						if (stringMap.containsKey(singleStr)) {
							value = stringMap.get(singleStr);
							value++;
						}
						stringMap.put(singleStr, value);
					}
					StringAnalysisData strAnalysisData = new StringAnalysisData();

					int minLength = StringUtility.calculateMinLength(strList);
					int maxLength = StringUtility.calculateMaxLength(strList);
					int numOfUniqueValues = StringUtility.calculateNumberOfUniqueValues(strList);
					double percentOfUniqueValues = StringUtility.calculatePercentOfUniqueValues(strList);
					double percentOfNullValues = StringUtility.calculatePercentOfNullValues(strList);
					strAnalysisData.setKey(key);
					strAnalysisData.setSampleData(stringDataMap.get(key).get(0));
					strAnalysisData.setMinLength(decimalFormat.format(minLength));
					strAnalysisData.setMaxLength(decimalFormat.format(maxLength));
					strAnalysisData.setNumberOfUniqueValues(decimalFormat.format(numOfUniqueValues));
					strAnalysisData.setPercentOfUniqueValues(decimalFormat.format(percentOfUniqueValues));
					strAnalysisData.setPercentOfNullValues(decimalFormat.format(percentOfNullValues));
					strAnalysisData.setDataMap(stringMap);
					stringData.put(key, strAnalysisData);
				}

				Gson gson = new Gson();
				htmlData = gson.toJson(stringData);
				LOG.debug("String data analysis:" + htmlData);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return htmlData;
	}

	public String analyseDateData(Map<String, List<Date>> dateDataMap, Map<String, DateAnalysisData> dateData) {
		String htmlData = new String("");
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#0.00");
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);

			if (dateDataMap.size() > 0) {
				for (Iterator<String> iter = dateDataMap.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					List<Date> dateList = dateDataMap.get(key);
					Map<Date, Integer> dateMap = new HashMap<Date, Integer>();
					for (Iterator<Date> dtIterator = dateList.iterator(); dtIterator.hasNext();) {
						Date singleDate = dtIterator.next();
						Integer value = 1;
						if (dateMap.containsKey(singleDate)) {
							value = dateMap.get(singleDate);
							value++;
						}
						dateMap.put(singleDate, value);
					}
					DateAnalysisData dateAnalysisData = new DateAnalysisData();

					Date minDate = DateUtility.calculateMinDate(dateList);
					Date maxDate = DateUtility.calculateMaxDate(dateList);
					double percentOfNullValues = DateUtility.calculatePercentOfNullValues(dateList);

					dateAnalysisData.setKey(key);
					dateAnalysisData.setSampleData(dateDataMap.get(key).get(0).toString());
					dateAnalysisData.setMinDate(minDate);
					dateAnalysisData.setMaxDate(maxDate);
					dateAnalysisData.setPercentOfNullValues(decimalFormat.format(percentOfNullValues));
					dateAnalysisData.setDataMap(dateMap);

					dateData.put(key, dateAnalysisData);
				}

				Gson gson = new Gson();
				htmlData = gson.toJson(dateData);
				LOG.debug("Date data analysis:" + htmlData);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return htmlData;
	}

	public void refineGroupByColumns(Map<String, NumericalAnalysisData> numericalData,
			Map<String, StringAnalysisData> stringData, List<ListDataDefinition> lstDataDefinition) {
		List<SubsegmentSelector> lstSubsegments = new ArrayList<SubsegmentSelector>(3);
		for (int i = 1; i < 4; i++) {
			SubsegmentSelector sSelector = new SubsegmentSelector();
			sSelector.setPercentOfUniqueValues(100.0);
			sSelector.setPosition(i);
			lstSubsegments.add(sSelector);
		}

		for (Entry<String, NumericalAnalysisData> e : numericalData.entrySet()) {
			String columnName = e.getKey();
			NumericalAnalysisData numAnalysisData = e.getValue();
			String percentOfUniqueValues = numAnalysisData.getPercentOfUniqueValues();
			Double pUniqueValues = Double.parseDouble(percentOfUniqueValues);

			SubsegmentSelector firstSelector = lstSubsegments.get(0);
			SubsegmentSelector secondSelector = lstSubsegments.get(1);
			SubsegmentSelector thirdSelector = lstSubsegments.get(2);

			if (pUniqueValues < firstSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(secondSelector.getPercentOfUniqueValues());
				thirdSelector.setColumnName(secondSelector.getColumnName());

				secondSelector.setPercentOfUniqueValues(firstSelector.getPercentOfUniqueValues());
				secondSelector.setColumnName(firstSelector.getColumnName());

				firstSelector.setPercentOfUniqueValues(pUniqueValues);
				firstSelector.setColumnName(columnName);

			} else if (pUniqueValues < secondSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(secondSelector.getPercentOfUniqueValues());
				thirdSelector.setColumnName(secondSelector.getColumnName());

				secondSelector.setPercentOfUniqueValues(pUniqueValues);
				secondSelector.setColumnName(columnName);
			} else if (pUniqueValues < thirdSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(pUniqueValues);
				thirdSelector.setColumnName(columnName);
			}

		}

		for (Entry<String, StringAnalysisData> e : stringData.entrySet()) {
			String columnName = e.getKey();
			StringAnalysisData strAnalysisData = e.getValue();
			String percentOfUniqueValues = strAnalysisData.getPercentOfUniqueValues();
			Double pUniqueValues = Double.parseDouble(percentOfUniqueValues);

			SubsegmentSelector firstSelector = lstSubsegments.get(0);
			SubsegmentSelector secondSelector = lstSubsegments.get(1);
			SubsegmentSelector thirdSelector = lstSubsegments.get(2);

			if (pUniqueValues < firstSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(secondSelector.getPercentOfUniqueValues());
				thirdSelector.setColumnName(secondSelector.getColumnName());

				secondSelector.setPercentOfUniqueValues(firstSelector.getPercentOfUniqueValues());
				secondSelector.setColumnName(firstSelector.getColumnName());

				firstSelector.setPercentOfUniqueValues(pUniqueValues);
				firstSelector.setColumnName(columnName);

			} else if (pUniqueValues < secondSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(secondSelector.getPercentOfUniqueValues());
				thirdSelector.setColumnName(secondSelector.getColumnName());

				secondSelector.setPercentOfUniqueValues(pUniqueValues);
				secondSelector.setColumnName(columnName);
			} else if (pUniqueValues < thirdSelector.getPercentOfUniqueValues()) {
				thirdSelector.setPercentOfUniqueValues(pUniqueValues);
				thirdSelector.setColumnName(columnName);
			}

		}

		SubsegmentSelector firstSelector = lstSubsegments.get(0);
		SubsegmentSelector secondSelector = lstSubsegments.get(1);
		SubsegmentSelector thirdSelector = lstSubsegments.get(2);
		for (ListDataDefinition listDataDefinition : lstDataDefinition) {
			if (!listDataDefinition.getColumnName().equalsIgnoreCase(firstSelector.getColumnName())
					&& !listDataDefinition.getColumnName().equalsIgnoreCase(secondSelector.getColumnName())
					&& !listDataDefinition.getColumnName().equalsIgnoreCase(thirdSelector.getColumnName())) {
				listDataDefinition.setDgroup("N");
			}
		}
	}

	public List<String> getTableColumns(@RequestParam Long idDataSchema, @RequestParam String dataLocation,
			@RequestParam String tableName, String queryString, String isQuery, HttpSession session) {

		List<String> columns = new ArrayList<String>();

		ResultSet resultSetFromDb = null;

		List<ListDataSchema> listDataSchema = DataTemplateAddNewDAO.getListDataSchema(idDataSchema);
		String hostURI = listDataSchema.get(0).getIpAddress();
		String databaseSchema = listDataSchema.get(0).getDatabaseSchema();
		String userlogin = listDataSchema.get(0).getUsername();
		String password = listDataSchema.get(0).getPassword();
		String portName = listDataSchema.get(0).getPort();
		String domain = listDataSchema.get(0).getDomain();
		String serviceName = listDataSchema.get(0).getKeytab();
		String sslEnb = listDataSchema.get(0).getSslEnb();
		String sslTrustStorePath = listDataSchema.get(0).getSslTrustStorePath();
		String trustPassword = listDataSchema.get(0).getTrustPassword();
		String principle = listDataSchema.get(0).getDomain();
		String keytab = listDataSchema.get(0).getKeytab();
		String krb5conf = listDataSchema.get(0).getKrb5conf();
		String bigQueryProjectName = listDataSchema.get(0).getBigQueryProjectName();
		String privatekeyId = listDataSchema.get(0).getPrivatekeyId();
		String privatekey = listDataSchema.get(0).getPrivatekey();
		String clientId = listDataSchema.get(0).getClientId();
		String clientEmail = listDataSchema.get(0).getClientEmail();
		String datasetName = listDataSchema.get(0).getDatasetName();
		String azureClientId = listDataSchema.get(0).getAzureClientId();
		String azureClientSecret = listDataSchema.get(0).getAzureClientSecret();
		String azureTenantId = listDataSchema.get(0).getAzureTenantId();
		String azureServiceURI = listDataSchema.get(0).getAzureServiceURI();
		String azureFilePath = listDataSchema.get(0).getAzureFilePath();
		String jksPath = listDataSchema.get(0).getJksPath();

		String kmsAuthDisabled = listDataSchema.get(0).getKmsAuthDisabled();
		String schemaType = listDataSchema.get(0).getSchemaType();
		String schemaName = listDataSchema.get(0).getSchemaName();

		/*
		 * When KMS Authentication is enabled, db user details comes from logon manager
		 */
		if (kmsAuthDisabled != null && kmsAuthDisabled.equalsIgnoreCase("N")) {

			boolean flag = false;

			// Check if the schema type is KMS enabled
			if (DatabuckConstants.KMS_ENABLED_SCHEMA_TYPES.contains(schemaType)) {

				// Get the credentials from logon manager
				Map<String, String> conn_user_details = logonManager.getCredentialsFromLogonCmd(schemaName);

				// Validate the logon manager response for key
				boolean responseStatus = logonManager.validateLogonManagerResponseForDB(conn_user_details);

				if (responseStatus) {
					flag = true;
					hostURI = conn_user_details.get("hostname");
					portName = (schemaType.equalsIgnoreCase("Oracle"))
							? conn_user_details.get("port") + "/" + serviceName
							: conn_user_details.get("port");
					userlogin = conn_user_details.get("username");
					password = conn_user_details.get("password");
				}
			} else
				LOG.info("\n====>KMS Authentication is not supported for [" + schemaType + "] !!");

			if (!flag) {
				LOG.info(
						"\n====>Unable to get columns list, failed to fetch connection details from logon manager !!");
				return columns;
			}
		}
		
		if (dataLocation.equalsIgnoreCase("SnowFlake")) {
			try {
				Map<String, String> metadata = snowFlakeConnection.readTablesFromSnowflake(hostURI, databaseSchema,
						userlogin, password, tableName, queryString, portName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}

		if (dataLocation.equalsIgnoreCase("FileSystem Batch")) {
			try {

				Map<String, String> metadata = batchFilesystem.getTablecolumnsFromfilesystem(listDataSchema.get(0),
						tableName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("Oracle")) {
			try {
				if (isQuery != null && isQuery.equalsIgnoreCase("column_name")) {
					resultSetFromDb = oracleconnection.getQueryResultsFromOracle(hostURI, userlogin, password, portName,
							queryString);
					tableName = queryString;
				} else {
					resultSetFromDb = oracleconnection.getTableDataFromOracle(hostURI, userlogin, password, portName,
							tableName, databaseSchema);
				}

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("Postgres")) {
			try {
				resultSetFromDb = postgresConnection.getTableDataFromPostgres(hostURI, userlogin, password, portName,
						databaseSchema, tableName, sslEnb);

			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		if (dataLocation.equalsIgnoreCase("Vertica")) {
			try {
				resultSetFromDb = verticaconnection.getTableDataFromVertica(hostURI, userlogin, password, portName,
						tableName, databaseSchema);

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("MSSQL")) {
			try {
				resultSetFromDb = mSSQLConnection.getTableDataFromMSSQL(hostURI, userlogin, password, portName,
						tableName, databaseSchema);

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("MYSQL")) {
			try {

				resultSetFromDb = mysqlConnection.getTableDataFromMYSQL(hostURI, userlogin, password, portName,
						tableName,databaseSchema,sslEnb);

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("MSSQLActiveDirectory")) {
			try {
				resultSetFromDb = msSqlActiveDirectoryConnectionObject.getTableDataFromMSSQLActiveDirectory(hostURI,
						userlogin, password, portName, tableName, databaseSchema, domain);

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("Amazon Redshift")) {
			try {
				resultSetFromDb = amazonRedshiftConnection.getTableDataFromAmazonRedshift(hostURI, userlogin, password,
						portName, tableName, databaseSchema, domain);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}

		if (dataLocation.equalsIgnoreCase("Oracle RAC")) {
			try {
				resultSetFromDb = OracleRACConnection.getTableDataFromOracleRAC(hostURI, userlogin, password, portName,
						databaseSchema, domain, serviceName, tableName);

			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("Hive") || dataLocation.equalsIgnoreCase("Hive Kerberos")
				|| dataLocation.equalsIgnoreCase("ClouderaHive")) {
			try {
				boolean isKerberosEnabled = dataLocation.equals("Hive Kerberos") ? true : false;

				resultSetFromDb = hiveconnection.getTableDataFromHive(dataLocation, hostURI, userlogin, password, portName, tableName,
						databaseSchema, sslEnb, sslTrustStorePath, trustPassword, isKerberosEnabled, keytab, krb5conf,
						principle, jksPath);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if(dataLocation.equals("MapR Hive")) {
			// Get DomainId and Domain Name of the connection, under which it is created.
			Integer domainId = listDataSchema.get(0).getDomainId();
			String domainName = iTaskDAO.getDomainNameById(domainId.longValue());

			Map<String, String> metadata =null;

			String clusterPropertyCategory= listDataSchema.get(0).getClusterPropertyCategory();
			LOG.debug("\n====>clusterPropertyCategory:"+clusterPropertyCategory);

			if(clusterPropertyCategory!=null && !clusterPropertyCategory.trim().isEmpty()
					&& !clusterPropertyCategory.equalsIgnoreCase("cluster")){

				String token= remoteClusterAPIService.generateRemoteClusterAPIToken(idDataSchema);

				StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
				encryptor.setPassword("4qsE9gaz%!L@UMrK5myY");

				String encryptedToken = encryptor.encrypt(token);

				metadata = remoteClusterAPIService.getTableMetaDataByRemoteCluster(hostURI, databaseSchema, userlogin, password,
						portName, domainName, tableName, queryString,encryptedToken,idDataSchema);
			}
			else
				metadata = hiveconnection.getTableMetaDataFromMapRHive(hostURI, databaseSchema, userlogin, password,
						portName, domainName, tableName, queryString);
			if(metadata!=null && metadata.size() > 0)
				columns = new ArrayList<>(metadata.keySet());
			else
				columns = new ArrayList<>();
		}
		if (dataLocation.equalsIgnoreCase("BigQuery")) {
			try {
				Map<String, String> metadata = bigQueryConnection.readTablesFromBigQuery(bigQueryProjectName,
						privatekeyId, privatekey, clientId, clientEmail, datasetName, tableName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("S3 Batch")) {
			try {
				Map<String, String> metadata = s3BatchConnection.getMetadataOfTableForS3(listDataSchema.get(0),
						tableName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("S3 IAMRole Batch")) {
			try {
				Map<String, String> metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName,
						listDataSchema.get(0), null, null, "");
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("S3 IAMRole Batch Config")) {
			try {
				Map<String, String> metadata = s3BatchConnection.getMetadataOfTableForS3IAMRole(tableName,
						listDataSchema.get(0), null, null, "");
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("AzureSynapseMSSQL")) {
			try {
				Map<String, String> metadata = azureConnection.readTablesFromAzureSynapse(hostURI, portName,
						databaseSchema, userlogin, password, tableName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (dataLocation.equalsIgnoreCase("AzureDataLakeStorageGen1")) {
			try {
				Map<String, String> metadata = azureDataLakeConnection.readColumsFromDataLake(azureClientId,
						azureClientSecret, azureTenantId, azureServiceURI, azureFilePath, tableName);
				if (metadata != null) {
					columns = new ArrayList<String>(metadata.keySet());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (resultSetFromDb != null) {
			try {
				LOG.debug("Table has Data? =" + resultSetFromDb.next());
				ResultSetMetaData metaData = resultSetFromDb.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String columnName = metaData.getColumnName(i);
					
					if (columnName.contains(".")) {
						String[] split = columnName.split("\\.");
						columnName = split[1];
					}
					
					columns.add(columnName);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
		LOG.debug("\n====> Metadata Columns: " + columns);
		return columns;
	}

	/**
	 * get table columns from json Object
	 * 
	 * @param file
	 * @return
	 */
	public Map<String, String> getTableColumnsForFile(MultipartFile file) {
		Map<String, String> columns = new HashMap();

		// parse json objet file
		JSONObject obj;
		try {

			JSONTokener tokener = new JSONTokener(new InputStreamReader(file.getInputStream(), "UTF-8"));
			// parse JSON object
			obj = new JSONObject(tokener);

			obj.keySet().forEach(keyStr -> {
				String keyvalue = obj.get(keyStr).toString();
				LOG.debug("key: " + keyStr + " value: " + keyvalue);
				columns.put(keyStr, keyvalue);

				// for nested objects iteration if required
				// if (keyvalue instanceof JSONObject)
				// printJsonObject((JSONObject)keyvalue);
			});

			LOG.debug(" columns " + columns);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return columns;
	}

}
