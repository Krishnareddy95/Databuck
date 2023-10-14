package com.databuck.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;


import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.databuck.bean.ListDataSchema;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.dao.impl.ImportExportUtilityDAOImpl;


@Service
public class ImportUtility {

	@Autowired
	public JdbcTemplate jdbcTemplate;
	
	@Autowired
	public IValidationCheckDAO validationCheckDao;

	@Autowired
	public Properties dbDependencyProperties;
	
	@Autowired
	public Properties appDbConnectionProperties;

	@Autowired
	private SchemaDAOI schemadao;
	
	@Autowired
	private ImportExportUtilityDAOImpl importExpoDao;
	
	private static final Logger LOG = Logger.getLogger(ImportUtility.class);

	String sql = null;
	int lddCounter = 0;
	
	
	
	public boolean importSelectedDataDirect(String fileName, Long projectId, String importIntoExistingDC, Long idDataSchema, MultipartFile file, Integer domainId)
			throws DataAccessException, Exception {
		Map<String, String> returnMsgsMap = new HashMap();
		boolean importstatus = true;
		try {
		String osname = System.getProperty("os.name", "").toLowerCase();

		LOG.debug("osname =>" + osname);
		
		
		if (osname.startsWith("windows")) {

			BufferedReader br = new BufferedReader(
					new FileReader(System.getProperty("user.home") + "/Desktop/" + fileName));

			LOG.info("In importSelectedData............");

			importDataWithDependenciesDirect( br,projectId,importIntoExistingDC,idDataSchema, domainId);

			br.close();

		} 
		 else if (osname.startsWith("linux")) {
			 
			 if(appDbConnectionProperties.getProperty("exportImportMode").equals("Y")) {
				 
				 copyFileFromLocalToServer(file);
			 }

			BufferedReader br = new BufferedReader(
					new FileReader(System.getProperty("user.home") + "/" + fileName));

			String desktopPath = System.getProperty("user.home") + "/";
			System.out.print("----------linux-----desktopPath =>" + desktopPath.replace("\\", "/"));
			
			importDataWithDependenciesDirect( br,projectId,importIntoExistingDC,idDataSchema, domainId);
			
			br.close();

		}

	} catch (FileNotFoundException e) {
		LOG.error("Problem creating outputstream from given file name.");
		LOG.error(e.getMessage());
		importstatus =false;
		returnMsgsMap.put("message", "Problem in importing data..");
   
	}
	return importstatus;
	}
	
	private void copyFileFromLocalToServer(MultipartFile file) throws IOException {
		
		//InputStream inputstreamfile = file.getInputStream();
		byte[] bytes = file.getBytes();
		OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.home") + "/" +
			    file.getOriginalFilename()));
		outputStream.write(bytes);
		outputStream.close();
		java.io.File savedFile = new java.io.File(System.getProperty("user.home") + "/" +
			    file.getOriginalFilename());
		LOG.debug("Saved file size is "+savedFile.length());
		}

	public boolean importSelectedData(String fileName, JdbcTemplate jdbcTemplateForImport, Long projectId)
			throws DataAccessException, Exception {
		
		Map<String, String> returnMsgsMap = new HashMap();
		try {

			/*
			 * BufferedReader br = new BufferedReader( new
			 * FileReader(System.getenv("DATABUCK_HOME") + "/exportData/" + fileName));
			 * 
			 */
			
			String osname = System.getProperty("os.name", "").toLowerCase();

			LOG.debug("osname =>" + osname);

			if (osname.startsWith("windows")) {

				BufferedReader br = new BufferedReader(
						new FileReader(System.getProperty("user.home") + "/Desktop/" + fileName));

				LOG.info("In importSelectedData............");

				importDataWithDependencies(null, br, jdbcTemplateForImport,projectId);

//				returnMsgsMap.put("message", "Data imported Successfully..");
				
				br.close();

			} else if (osname.startsWith("linux")) {

				BufferedReader br = new BufferedReader(
						new FileReader(System.getProperty("user.home") + "/" + fileName));

				String desktopPath = System.getProperty("user.home") + "/";
				LOG.debug("----------linux-----desktopPath =>" + desktopPath.replace("\\", "/"));
				
				importDataWithDependencies(null, br, jdbcTemplateForImport,projectId);
				
//				returnMsgsMap.put("message", "Data imported Successfully..");

				br.close();

			}

		} catch (FileNotFoundException e) {
			LOG.error("Problem creating outputstream from given file name.");
			LOG.error(e.getMessage());
			returnMsgsMap.put("message", "Problem in importing data..");

		}
		return true;
	}

	
	
	
	
	private void importDataWithDependenciesDirect(BufferedReader br, Long projectId,String importIntoExistingDC, Long idDataSchema, Integer domainId) throws DataAccessException, Exception 
	{
		
		String s;
		s = br.readLine();
		if(importIntoExistingDC.equalsIgnoreCase("Y")){
		
			if(s.equalsIgnoreCase("#DT:"))
			{
				long idData = 0l;	
				long idschema = 0l;
				long idDataColGlobalRule = 0l;
				
				while ((s = br.readLine()) != null) {
					
					
				
				if(s.equalsIgnoreCase("#DT:"))	
				{
					s = br.readLine();
				}
				if(s.equalsIgnoreCase("#LDS:"))
				{
					s = br.readLine();
					jdbcTemplate.execute(importExpoDao.insertIntoListDataSourcesForImportdirect(s,projectId,idDataSchema));
				   //getlatestidata
					idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
							Long.class);
				
				}
				if(s.equalsIgnoreCase("#LDA:"))
				{
					s = br.readLine();
					//passlatestiddata  
					jdbcTemplate.execute(importExpoDao.insertIntoListDataAccessForImportdirect(s,idData,idDataSchema));

				}
				
				if(s.equalsIgnoreCase("#LDD:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListDataDefinationForImportdirect(s,idData));	
				
				}
				if(s.equalsIgnoreCase("#LDR:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListColRulesForImportdirect(s,idData,projectId));	
				
				}
				if(s.equalsIgnoreCase("#LDGR:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListColGlobalRulesForImportdirect(s,idData,projectId));
						
				idDataColGlobalRule = importExpoDao.readGlobalRuleId(s,idData);	
				
				}
				if(s.equalsIgnoreCase("#LDGRS:"))
				{
				//passlatestiddata
					s = br.readLine();
					importExpoDao.insertIntoSynonymLibraryImportdirect(s,idData,projectId,idDataColGlobalRule);	
				
				}
				
			/*	if(s.equalsIgnoreCase("#CN:"))
				{
					s = br.readLine();
					jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId));
						
					//getlatestiddatschema
		          //update LDS LDA iddatschema where iddata = latestidata
					
					idschema = jdbcTemplate.queryForObject("select max(idDataSchema) from listDataSchema",
							Long.class);
					String updateQuery = "update listDataSources set idDataSchema = ? where idData = ?";
					jdbcTemplate.update(updateQuery, idschema, idData);
					updateQuery = "update listDataAccess set idDataSchema = ? where idData = ?";
					jdbcTemplate.update(updateQuery, idschema, idData);
				}*/
				
				
				}
			}else if(s.equalsIgnoreCase("#VC:"))
			{
				long idData = 0l;	
			    long idschema = 0l;
			    long idApp = 0l;
			    long idDataColGlobalRule = 0l;
			    boolean iterator = true;
			    idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
						Long.class);
			while ((s = br.readLine()) != null) {
				
				if(iterator)	
				{
				jdbcTemplate.execute(importExpoDao.insertIntoListApplicationsForImportdirect(s,projectId,idData));
				iterator =false;
				//getlatestidapp
				idApp = jdbcTemplate.queryForObject("select max(idApp) from listApplications",
						Long.class);
				
				}
				if(s.equalsIgnoreCase("#VC:"))	
				{
					s = br.readLine();
				jdbcTemplate.execute(importExpoDao.insertIntoListApplicationsForImportdirect(s,projectId,idData));
				iterator =false;
				//getlatestidapp
				idApp = jdbcTemplate.queryForObject("select max(idApp) from listApplications",
						Long.class);
				
				}
				
				if(s.equalsIgnoreCase("#DT:"))	
				{
					s = br.readLine();
				}
				
				if(s.equalsIgnoreCase("#LDS:"))
				{
					s = br.readLine();
					jdbcTemplate.execute(importExpoDao.insertIntoListDataSourcesForImportdirect(s,projectId,idDataSchema));
				   //getlatestidata
					idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
							Long.class);
					//setlatestidApp with iddata
					String updateQuery = "update listApplications set idData = ? where idApp = ?";
					jdbcTemplate.update(updateQuery, idData, idApp);
				}
				if(s.equalsIgnoreCase("#LDA:"))
				{
					s = br.readLine();
					//passlatestiddata  
					jdbcTemplate.execute(importExpoDao.insertIntoListDataAccessForImportdirect(s,idData,idDataSchema));

				}
				
				if(s.equalsIgnoreCase("#LDD:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListDataDefinationForImportdirect(s,idData));	
				
				}
				if(s.equalsIgnoreCase("#LDR:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListColRulesForImportdirect(s,idData,projectId));	
				
				}
				if(s.equalsIgnoreCase("#LDGR:"))
				{
				//passlatestiddata
					s = br.readLine();
						jdbcTemplate.execute(
								importExpoDao.insertIntoListColGlobalRulesForImportdirect(s,idData,projectId));
						
				idDataColGlobalRule = importExpoDao.readGlobalRuleId(s,idData);	
				
				}
				if(s.equalsIgnoreCase("#LDGRS:"))
				{
				//passlatestiddata
					s = br.readLine();
					importExpoDao.insertIntoSynonymLibraryImportdirect(s,idData,projectId,idDataColGlobalRule);	
				
				}
				
			}
			}
			
			
		} else {
		
			
			
			if(s.equalsIgnoreCase("#CN:"))
		{
			while ((s = br.readLine()) != null) {
				
				if(s.equalsIgnoreCase("#CN:"))
				{
					s = br.readLine();
				}
				jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId, domainId));	
			}
			
		}
			
		
		
		else if(s.equalsIgnoreCase("#DT:"))
		{
			long idData = 0l;	
			long idschema = 0l;
			long idDataColGlobalRule = 0l;
			
			while ((s = br.readLine()) != null) {
				
				
			
			if(s.equalsIgnoreCase("#DT:"))	
			{
				s = br.readLine();
			}
			if(s.equalsIgnoreCase("#LDS:"))
			{
				s = br.readLine();
				jdbcTemplate.execute(importExpoDao.insertIntoListDataSourcesForImportdirect(s,projectId,idschema));
			   //getlatestidata
				idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
						Long.class);
			
			}
			if(s.equalsIgnoreCase("#LDA:"))
			{
				s = br.readLine();
				//passlatestiddata  
				jdbcTemplate.execute(importExpoDao.insertIntoListDataAccessForImportdirect(s,idData,idschema));

			}
			
			if(s.equalsIgnoreCase("#LDD:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListDataDefinationForImportdirect(s,idData));	
			
			}
			if(s.equalsIgnoreCase("#LDR:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListColRulesForImportdirect(s,idData,projectId));	
			
			}
			if(s.equalsIgnoreCase("#LDGR:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListColGlobalRulesForImportdirect(s,idData,projectId));
					
			idDataColGlobalRule = importExpoDao.readGlobalRuleId(s,idData);	
			
			}
			if(s.equalsIgnoreCase("#LDGRS:"))
			{
			//passlatestiddata
				s = br.readLine();
				importExpoDao.insertIntoSynonymLibraryImportdirect(s,idData,projectId,idDataColGlobalRule);	
			
			}
			
			if(s.equalsIgnoreCase("#CN:"))
			{
				s = br.readLine();
				jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId, domainId));
					
				//getlatestiddatschema
	          //update LDS LDA iddatschema where iddata = latestidata
				
				idschema = jdbcTemplate.queryForObject("select max(idDataSchema) from listDataSchema",
						Long.class);
				String updateQuery = "update listDataSources set idDataSchema = ? where idData = ?";
				jdbcTemplate.update(updateQuery, idschema, idData);
				updateQuery = "update listDataAccess set idDataSchema = ? where idData = ?";
				jdbcTemplate.update(updateQuery, idschema, idData);
			}
			
			
			}
		}
		
		
		else if(s.equalsIgnoreCase("#VC:"))
		{
			long idData = 0l;	
		    long idschema = 0l;
		    long idApp = 0l;
		    long idDataColGlobalRule = 0l;
		    boolean iterator = true;
		    idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
					Long.class);
		while ((s = br.readLine()) != null) {
			
			if(iterator)	
			{
			jdbcTemplate.execute(importExpoDao.insertIntoListApplicationsForImportdirect(s,projectId,idData));
			iterator =false;
			//getlatestidapp
			idApp = jdbcTemplate.queryForObject("select max(idApp) from listApplications",
					Long.class);
			
			}
			if(s.equalsIgnoreCase("#VC:"))	
			{
				s = br.readLine();
			jdbcTemplate.execute(importExpoDao.insertIntoListApplicationsForImportdirect(s,projectId,idData));
			iterator =false;
			//getlatestidapp
			idApp = jdbcTemplate.queryForObject("select max(idApp) from listApplications",
					Long.class);
			
			}
			
			if(s.equalsIgnoreCase("#DT:"))	
			{
				s = br.readLine();
			}
			if(s.equalsIgnoreCase("#LDS:"))
			{
				s = br.readLine();
				jdbcTemplate.execute(importExpoDao.insertIntoListDataSourcesForImportdirect(s,projectId,idschema));
			   //getlatestidata
				idData = jdbcTemplate.queryForObject("select max(idData) from listDataSources",
						Long.class);
				//setlatestidApp with iddata
				String updateQuery = "update listApplications set idData = ? where idApp = ?";
				jdbcTemplate.update(updateQuery, idData, idApp);
			}
			if(s.equalsIgnoreCase("#LDA:"))
			{
				s = br.readLine();
				//passlatestiddata  
				jdbcTemplate.execute(importExpoDao.insertIntoListDataAccessForImportdirect(s,idData,idschema));

			}
			
			if(s.equalsIgnoreCase("#LDD:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListDataDefinationForImportdirect(s,idData));	
			
			}
			if(s.equalsIgnoreCase("#LDR:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListColRulesForImportdirect(s,idData,projectId));	
			
			}
			if(s.equalsIgnoreCase("#LDGR:"))
			{
			//passlatestiddata
				s = br.readLine();
					jdbcTemplate.execute(
							importExpoDao.insertIntoListColGlobalRulesForImportdirect(s,idData,projectId));
					
			idDataColGlobalRule = importExpoDao.readGlobalRuleId(s,idData);	
			
			}
			if(s.equalsIgnoreCase("#LDGRS:"))
			{
			//passlatestiddata
				s = br.readLine();
				importExpoDao.insertIntoSynonymLibraryImportdirect(s,idData,projectId,idDataColGlobalRule);	
			
			}
			
			if(s.equalsIgnoreCase("#CN:"))
			{
				s = br.readLine();
				jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId, domainId));
					
				//getlatestiddatschema
	          //update LDS LDA iddatschema where iddata = latestidata
				
				idschema = jdbcTemplate.queryForObject("select max(idDataSchema) from listDataSchema",
						Long.class);
				String updateQuery = "update listDataSources set idDataSchema = ? where idData = ?";
				jdbcTemplate.update(updateQuery, idschema, idData);
				updateQuery = "update listDataAccess set idDataSchema = ? where idData = ?";
				jdbcTemplate.update(updateQuery, idschema, idData);
			}
			
		}
		}
		}
	/*	while ((s = br.readLine()) != null) {
		
		LOG.debug("current Read Line : "+ s);
		if(s.equalsIgnoreCase("#CN:"))
		{
			s = br.readLine();
			jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId));
			
		}
		else
		{
			jdbcTemplate.execute(importExpoDao.insertIntoConnectionForImport(s,projectId));
			
		}
		}*/
	}
	
	
	private void importDataWithDependencies(String type, BufferedReader br, JdbcTemplate jdbcTemplateForImport, Long projectId)
			throws DataAccessException, Exception {
		String s;
		String typeDependency;
		Boolean exitIndicator = false;
		
		Map<String, String> getImportCompleteData = new HashMap<String, String>();
		Map<String, String> importMap = new HashMap<String, String>();

		LOG.info("........ importDataWithDependencies.............");
		
		Map<String, String> returnMsgsMap = new HashMap();
		
		while ((s = br.readLine()) != null) {

			if (s.equalsIgnoreCase("#DT:")) {
				continue;
			} else {
				type = s;
			}
			typeDependency = dbDependencyProperties.getProperty(type.replace("#", "").replace(":", ""));

			/*
			 * if (s.equalsIgnoreCase("#DT:")) { continue; } else if (null != type &&
			 * s.equalsIgnoreCase("#CN:")) { typeDependency = ""; type = s; exitIndicator =
			 * true; } else { type = s;
			 * 
			 * 
			 * 
			 * }
			 * 
			 * 
			 *//* if (typeDependency != null && !typeDependency.equals("-")) {
				getImportCompleteData.putAll(importData(type, br.readLine()));
			} else {
				LOG.debug("In else =>"+exitIndicator);
			//	String line
				getImportCompleteData.putAll(importData(type, br.readLine()));
				exitIndicator = true;
				LOG.debug("After Setting exitIndicator =>"+exitIndicator);
			}*/
			
			LOG.debug("typeDepe ->"+typeDependency);
			
			if (typeDependency != null && !typeDependency.trim().equals("")) {
				getImportCompleteData.putAll(importData(importMap, type, br.readLine()));
			} else {
				getImportCompleteData.putAll(importData(importMap, type, br.readLine()));
				exitIndicator = true;
			}
			if (exitIndicator) {
				Long idSchema = 0L;
				Long idData = 0L;
				if (getImportCompleteData.containsKey("CN")) {

					LOG.debug("getImportCompleteData.get(\"CN\") =>" + getImportCompleteData.get("CN"));

					if(getImportCompleteData.get("CN") != null && !getImportCompleteData.get("CN").equals("")){
						jdbcTemplateForImport.execute(importExpoDao.insertIntoConnectionForImport(getImportCompleteData.get("CN"),projectId, 0));
						if (jdbcTemplateForImport.queryForRowSet("select max(idDataSchema) from listDataSchema").next()) {
							idSchema = jdbcTemplateForImport.queryForObject("select max(idDataSchema) from listDataSchema",
									Long.class);
						}
					}
				}
				if (getImportCompleteData.containsKey("LDS")) {
					jdbcTemplateForImport
							.execute(importExpoDao.insertIntoListDataSourcesForImport(getImportCompleteData.get("LDS"), idSchema,projectId));
					if (jdbcTemplateForImport.queryForRowSet("select max(idData) from listDataSources").next()) {
						idData = jdbcTemplateForImport.queryForObject("select max(idData) from listDataSources",
								Long.class);
					}
					jdbcTemplateForImport
							.execute(importExpoDao.insertIntoListDataAccessForImport(getImportCompleteData.get("LDA"), idSchema, idData));
					for (int i = 0; i < lddCounter; i++) {
						jdbcTemplateForImport.execute(
								importExpoDao.insertIntoListDataDefinationForImport(getImportCompleteData.get("LDD" + i), idSchema, idData));
					}
				}

				lddCounter = 0;
				if (getImportCompleteData.containsKey("VC")) {
					jdbcTemplateForImport.execute(importExpoDao.insertIntoListApplicationsForImport(getImportCompleteData.get("VC"), idData,projectId));
				}
				exitIndicator = false;
			}
		}
			
	}

	public Map<String, String> importData(Map<String, String> importMap, String type, String rowToImport) throws IOException {
		Object obj = new Object();

		LOG.info("ImportUtility.............. importData");

		if (type.equals("#VC:")) {
			// importMap.put("VC", insertIntoListApplications(rowToImport));
			importMap.put("VC", rowToImport);
		} else if (type.equals("#LDS:")) {
			// importMap.put("LDS", insertIntoListDataSources(rowToImport));
			importMap.put("LDS", rowToImport);
		} else if (type.equals("#LDA:")) {
			// importMap.put("LDA", insertIntoListDataAccess(rowToImport));
			importMap.put("LDA", rowToImport);
		} else if (type.equals("#LDD:")) {
			// importMap.put("LDD"+lddCounter, insertIntoListDataDefination(rowToImport));
			importMap.put("LDD" + lddCounter, rowToImport);
			lddCounter++;
		} else if (type.equals("#CN:")) {
			// importMap.put("CN", insertIntoConnection(rowToImport));
			importMap.put("CN", rowToImport);
		}
		return importMap;
	}

	private Map<String, String> getMap(String readLine) {
		// TODO Auto-generated method stub
		Map<String, String> myMap = new HashMap<String, String>();
		String[] pairs = readLine.split(",");
		for (int i = 0; i < pairs.length; i++) {
			String pair = pairs[i];
			String[] keyValue = pair.split("=");
			if (keyValue.length == 2) {
				LOG.debug(keyValue[0] + " " + keyValue[1]);
				myMap.put(keyValue[0], "'" + keyValue[1] + "'");
			} else {
				LOG.debug(keyValue[0] + " " + "null");
				myMap.put(keyValue[0], "''");
			}

		}
		return myMap;
	}

	



	

	
	
}
