package com.databuck.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListColGlobalRules;
import com.databuck.bean.ListColRules;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.SynonymLibrary;
import com.databuck.bean.listDataAccess;
import com.databuck.dao.IImportExportUtilityDAO;
import com.opencsv.CSVWriter;
import org.apache.log4j.Logger;

@Service
public class ExportUtility {

	@Autowired
	public Properties dbDependencyProperties;

	@Autowired
	public IImportExportUtilityDAO importExportUtilityDAO;
	
	private static final Logger LOG = Logger.getLogger(ExportUtility.class);

	public boolean exportSelectedData(String type, Long id, String fileName) {
		boolean exportstatus = true;
		try {
			
			String osname = System.getProperty("os.name", "").toLowerCase();

			LOG.debug("osname =>" + osname);

			// Added OS checks.. for Windows and Linux 25Jan2019
			
		
			if (osname.startsWith("windows")) {
			
				FileOutputStream fos = new FileOutputStream(
						new File(System.getProperty("user.home") + "/Desktop/" + fileName),true);
			
				String desktopPath = System.getProperty("user.home") + "/Desktop";
				System.out.print("----------------desktopPath =>" + desktopPath.replace("\\", "/"));

				LOG.debug(" In exportSelectedData =>" + type + "exportSelectedData ID=>" + id);

				PrintWriter pw = new PrintWriter(fos);

				exportDataWithDependencies(type, null, id, pw);
				pw.close();
				
			} else if (osname.startsWith("linux")) {

				FileOutputStream fos= new FileOutputStream(
						new File(System.getProperty("user.home") + "/" + fileName),true);

				String desktopPath = System.getProperty("user.home") + "/";
				System.out.print("----------linux-----desktopPath =>" + desktopPath.replace("\\", "/"));
				LOG.debug(" In linux exportSelectedData =>" + type + "exportSelectedData ID=>" + id);

				PrintWriter pw = new PrintWriter(fos);

				exportDataWithDependencies(type, null, id, pw);
				pw.close();
			}
		} catch (FileNotFoundException e) {
			LOG.error("Problem creating outputstream from given file name.");
			LOG.error(e.getMessage());
			exportstatus =false;
		}

		return exportstatus;
	}

	private void exportDataWithDependencies(String type, String nextIdField, Long id, PrintWriter pw) {
		Long nextId = 01L;
		nextId = exportData(type, nextIdField, id, pw);

		String typeDependency = dbDependencyProperties.getProperty(type);
		if (typeDependency != null) {
			String[] dependencyList = typeDependency.split(",");
			if (dependencyList != null && dependencyList.length > 0) {
				for (int i = 0; i < dependencyList.length; i++) {
					if(!dependencyList[i].trim().equals("")) {
					String[] dependencyInfo = dependencyList[i].split("-");
					String nextIdFieldFromProp = dependencyInfo[1];
					exportDataWithDependencies(dependencyInfo[0], nextIdFieldFromProp, nextId, pw);
					}
				}
			}
		}
	}

	private Long exportData(String type, String nextIdField, Long id, PrintWriter pw) throws NullPointerException {

		Long nextId = -1L;
		Object obj = new Object();
		List<String> fldListWithValues = new ArrayList<String>();
		LOG.debug("dbDependencyProperties.getProperty(type)  " + type);
		LOG.debug("dbDependencyProperties.getProperty(type)  " + dbDependencyProperties.getProperty(type));
		
		/*if(!pw.equals(null)) {
			LOG.debug("pw =>"+pw.append(""));
		}*/
		if (null != dbDependencyProperties.getProperty(type) && !dbDependencyProperties.getProperty(type).trim().equals("")) {
			nextIdField = dbDependencyProperties.getProperty(type).split(",")[0].split("-")[1];
			LOG.debug("nextIdField =>"+nextIdField);
		}
		if (type.equals("VC")) {
			pw.println("#VC:");
			obj = new ListApplications();
			obj = importExportUtilityDAO.getdatafromlistapplications(id);
			fldListWithValues = getFieldListWithValues(obj);
			String fieldString = String.join("|", fldListWithValues);
			pw.println(fieldString);

			if (nextIdField != null) {
				nextId = getNextIdForObj(obj, nextIdField);
			}
		} else if (type.equals("DT")) {
			pw.println("#DT:");
			pw.println("#LDS:");
			obj = new ListDataSource();
			obj = importExportUtilityDAO.getDataFromListDataSources(id);
			fldListWithValues = getFieldListWithValues(obj);
			String fieldString = String.join("|", fldListWithValues);
			pw.println(fieldString);
			obj = new listDataAccess();
			pw.println("#LDA:");
			obj = importExportUtilityDAO.getListDataAccess(id);
			fldListWithValues = getFieldListWithValues(obj);
			fieldString = String.join("|", fldListWithValues);
			pw.println(fieldString);
			if (nextIdField != null) {
				nextId = getNextIdForObj(obj, nextIdField);
			}
			obj = new ListDataDefinition();

			ListIterator<ListDataDefinition> li = importExportUtilityDAO.getListDataDefinitionData(id).listIterator();
			while (li.hasNext()) {
				pw.println("#LDD:");
				obj = li.next();
				fldListWithValues = getFieldListWithValues(obj);
				fieldString = String.join("|", fldListWithValues);
				pw.println(fieldString);
			}
			ListIterator<ListColRules> lr = importExportUtilityDAO.getListColRulesData(id).listIterator();
			while (lr.hasNext()) {
				pw.println("#LDR:");
				obj = lr.next();
				fldListWithValues = getFieldListWithValues(obj);
				fieldString = String.join("|", fldListWithValues);
				pw.println(fieldString);
			}
			ListIterator<ListColGlobalRules> lg = importExportUtilityDAO.getListColGlobalRulesData(id).listIterator();
			while (lg.hasNext()) {
				pw.println("#LDGR:");
				obj = lg.next();
				fldListWithValues = getFieldListWithValues(obj);
				fieldString = String.join("|", fldListWithValues);
				pw.println(fieldString);
			}
			ListIterator<SynonymLibrary> lgs = importExportUtilityDAO.getListSynonymLibraryData(id).listIterator();
			while (lgs.hasNext()) {
				pw.println("#LDGRS:");
				obj = lgs.next();
				fldListWithValues = getFieldListWithValues(obj);
				fieldString = String.join("|", fldListWithValues);
				pw.println(fieldString);
			}
		} else if (type.equals("CN")) {
			pw.println("#CN:");
			if (id > 0) {				
				obj = new ListDataSchema();
				obj = importExportUtilityDAO.getListDataSchema(id).get(0);
				fldListWithValues = getFieldListWithValues(obj);
				String fieldString = String.join("|", fldListWithValues);
				pw.println(fieldString);

				if (nextIdField != null) {
					nextId = getNextIdForObj(obj, nextIdField);
				}
			}
		}

		return nextId;
	}

	private List<String> getFieldListWithValues(Object obj) {
		Class objClass = obj.getClass();
		LOG.debug(objClass.getName());
		Field[] fieldList = objClass.getDeclaredFields();
		// Field[] fieldList = objClass.getFields();
		List<String> fldListWithValues = new ArrayList<String>();
		for (int i = 0; i < fieldList.length; i++) {
			String fldString = "";
			try {
				fieldList[i].setAccessible(true);
				fldString = fieldList[i].getName() + ":=" + fieldList[i].get(obj);
			} catch (Exception e) {
				LOG.error("Exception while retrieving field value:" + e.getMessage());
			}
			if (fldString.length() > 0) {
				fldListWithValues.add(fldString);
			}
		}

		return fldListWithValues;
	}

	private Long getNextIdForObj(Object obj, String nextIdField) {
		Class objClass = obj.getClass();
		Field[] fieldList = objClass.getDeclaredFields();
		Long nextId = -1L;
		for (int i = 0; i < fieldList.length; i++) {
			try {
				fieldList[i].setAccessible(true);
				if (fieldList[i].getName().equalsIgnoreCase(nextIdField)) {
					nextId = Long.valueOf(fieldList[i].get(obj).toString());
				}
			} catch (Exception e) {
				LOG.error("Exception while retrieving field value:" + fieldList[i] + ":" + e.getMessage());
			}

		}
		return nextId;
	}

	public void writeToCsv(List<String[]> aDataList, String sCsvFullFileName, char sColumnDelimeter) {		
		CSVWriter oCsvWrite = null; 
		
		try {			
			oCsvWrite = new CSVWriter(new FileWriter(sCsvFullFileName), ',');
			for (String[] oData : aDataList) {
				oCsvWrite.writeNext(oData);
			}
			oCsvWrite.close();
		} catch (Exception oException) {
			LOG.error(oException.getMessage());
		}
	}	
	
}
