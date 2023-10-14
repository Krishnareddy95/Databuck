package com.databuck.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.IValidationCheckDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.RBACController;
import com.databuck.util.ConnectionUtil;
import com.databuck.util.ExportUtility;
import com.databuck.util.ImportUtility;

@Controller
public class ImportExportController {
	@Autowired
	private RBACController rbacController;

	@Autowired
	private IListDataSourceDAO listdatasourcedao;

	@Autowired
	private SchemaDAOI schemadao;

	@Autowired
	ExportUtility exportUtility;

	@Autowired
	ConnectionUtil connectionUtil;

	@Autowired
	public Properties appDbConnectionProperties;

	@Autowired
	ImportUtility importUtility;

	@Autowired
	IValidationCheckDAO validationcheckdao;

	Long idDataSchema;

	// private static final String UPLOAD_DIRECTORY
	// ="D://DateRuleChanges_14Jan2019//";

	@RequestMapping("uploadform")
	public ModelAndView uploadForm() {
		return new ModelAndView("uploadform");
	}

	@RequestMapping(value = "/savefile", method = RequestMethod.POST)
	public ModelAndView saveimage(ModelAndView model, @RequestParam CommonsMultipartFile file, HttpSession session)
			throws Exception {

		System.out.println("In ImportExportController ......... saveFile");

		ServletContext context = session.getServletContext();
		// String path = context.getRealPath("C:/Priyanka/exportData/");\

		String path = "C:/Priyanka/exportData/";

		System.out.println("In ImportExportController ... Path=>" + path);

		String filename = file.getOriginalFilename();

		System.out.println("In ImportExportController ... filename=>" + path + filename);

		// boolean res = importUtility.importSelectedData(filename);

		// System.out.println("----- importUtility.importSelectedData res-------" +
		// res);

		/*
		 * byte[] bytes = file.getBytes(); BufferedOutputStream stream =new
		 * BufferedOutputStream(new FileOutputStream( new File(path + File.separator +
		 * filename))); stream.write(bytes); stream.flush(); stream.close();
		 */

		/*
		 * ModelAndView modelAndView = new ModelAndView("importAndExport");
		 * 
		 * return modelAndView;
		 */
		model.setViewName("importAndExport");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "importAndExport");

		return model;
	}

	// Added Import And Export [Priyanka 26 December 2018]

	@RequestMapping(value = "/importAndExport")
	public ModelAndView importAndExport(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("User Settings", "U", session);
		if (rbac) {
			model.setViewName("importAndExport");
			model.addObject("currentSection", "User Settings");
			model.addObject("currentLink", "importAndExport");

			return model;
		} else
			return new ModelAndView("loginPage");
	}

	// add fun
	@RequestMapping(value = "/exportCSVFileData")
	public ModelAndView exportCSVFileData(HttpServletRequest req, HttpSession session, HttpServletResponse response)
			throws IOException {

		System.out.println("In exportCSVFileData..........");
		JSONObject json = new JSONObject();

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Connection", "R", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("ExportSuccess");
			System.out.println("------- exportCSVFileData -id->" + req.getParameter("idDataIds"));
			System.out.println("--- exportCSVFileData ------ FileNAem =>" + req.getParameter("fileName") + ".txt");
			System.out.println("--- exportCSVFileData ------ Type =>" + req.getParameter("type"));
			/*
			 * System.out.println("newTemplateName is " + newTemplateName );
			 * System.out.println("idData is " + idData );
			 */
			String type = req.getParameter("type");

			// Long idDataIds = Long.parseLong(req.getParameter("idDataIds"));

			String idDataIds = req.getParameter("idDataIds");

			String[] arrIds = idDataIds.split(",");

			// Long id = (long) Integer.parseInt(idDataIds);
			// Long id = (long) 172;

			// long idDt = new Long(idDataIds.trim());

			String fileName = req.getParameter("fileName") + ".txt";

			boolean res = false;

			for (int i = 0; i < arrIds.length; i++) {

				System.out.println(" String[] arrIds =>" + arrIds[i]);
				Long tempId = new Long(arrIds[i].trim());
				res = exportUtility.exportSelectedData(type, tempId, fileName);
			}

			/*if (res) {
				if (appDbConnectionProperties.getProperty("exportImportMode").equals("Y")) {
					exportToLocalFromServer(response,req,fileName);
				}
				
			}*/

			System.out.println("Reult of exportSelectedData =>" + res);

			if (type.equalsIgnoreCase("CN")) {

				modelAndView.addObject("currentSection", "Data Connection");
				modelAndView.addObject("currentLink", "DCView");
				// modelAndView.setViewName("exportUI");
			} else if (type.equalsIgnoreCase("DT")) {

				modelAndView.addObject("currentSection", "Data Template");
				modelAndView.addObject("currentLink", "DTView");
				// modelAndView.setViewName("exportUI");
			} else {

				modelAndView.addObject("currentSection", "Validation Check");
				modelAndView.addObject("currentLink", "VCView");
				// modelAndView.setViewName("exportUI");
			}
			
			if (res) {
				System.out.println("In res.............");
				if (appDbConnectionProperties.getProperty("exportImportMode").equals("Y")) {
					exportToLocalFromServer(response,req,fileName);
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
				
				modelAndView.addObject("message", "Exported Successfully");
				// modelAndView.setViewName("ExportSuccess");
				
				 json.put("success", "Exported Successfully");
				 response.getWriter().println(json);
				 

			} else {
				
				 json.put("fail", "Problem in exporting File");
				 response.getWriter().println(json);
				 

				modelAndView.addObject("message", "Problem in Exporting File");
			}

			return modelAndView;
		} else
			return new ModelAndView("loginPage");

	}

	private void exportToLocalFromServer(HttpServletResponse response, HttpServletRequest req, String fileName) throws IOException {
		boolean result = true;
		int totalFileLength = 0;
		String mimeType = "";
		// get output stream of the response
		
		OutputStream outStream = response.getOutputStream();
		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		if (outStream!=null) {
			outStream = response.getOutputStream();
			response.setHeader(headerKey, headerValue);
		}
		//response.setHeader(headerKey, headerValue);
		// get absolute path of the application
		ServletContext context = req.getSession().getServletContext();
		String folderName = System.getProperty("user.home");
		/*String folderName = appDbConnectionProperties.getProperty("exportImportLocalPath") + "/Desktop";*/ 

		System.out.println(fileName);

		byte[] buffer = new byte[1024 * 1000];
		int bytesRead = -1;
		if (fileName.endsWith(".txt")) {

			String fileFullPath = folderName + "/" + fileName;
			System.out.println("Export ........ File +" + fileName);

			String appPath = context.getRealPath("");
			System.out.println("ExportLocal ........ appPath = " + appPath);

			// construct the complete absolute path of the file
			File downloadFile = new File(fileFullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			mimeType = context.getMimeType(fileFullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			totalFileLength = totalFileLength + (int) downloadFile.length();
			System.out.println("MIME type: " + mimeType);
			System.out.println("downloadFile.length() =>" + downloadFile.length());

			// write bytes read from the input stream into the
			// output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			result = false;
			inputStream.close();

		}
		outStream.flush();
		outStream.close();
		
	}

	// New Import Export Ui Functionality 30jan2019

	@RequestMapping(value = "/getImportUI", method = RequestMethod.GET)
	public ModelAndView getImportUiView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "C", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			
			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId, projList, "", "");
			System.out.println("listdataschema=" + listdataschema);
			ModelAndView modelAndView = new ModelAndView("importUI");
			modelAndView.addObject("listdataschema", listdataschema);
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "importUI");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/submitImportUiForm", method = RequestMethod.POST)
	public @ResponseBody ModelAndView uploadImportFileHandler(@RequestParam("dataupload") MultipartFile file,
			HttpSession session, HttpServletRequest request, HttpServletResponse res)
			throws DataAccessException, Exception {

		Object user = session.getAttribute("user");
		long idUser = (Long) session.getAttribute("idUser");
		Long projectId = (Long) session.getAttribute("projectId");
		
		Integer domainId = (Integer) session.getAttribute("domainId");
		System.out.println("domainId at import UI: " + domainId);

		System.out.println("idUser=" + idUser);
		System.out.println("user:" + user);
		// Map finalMap = new HashMap();

		// String schemaName = request.getParameter("schemaName");
		boolean rbac = rbacController.rbac("Data Template", "C", session);
		if (rbac) {

			System.out.println("create form");
			// System.out.println("@RequestParam(\"dataupload\") MultipartFile file =>" +
			// file.getOriginalFilename());

			String schema = request.getParameter("chkIdDataSchema1");
			System.out.println("schema =>" + schema);

			idDataSchema = Long.valueOf(0L);
			if ((schema != null) && (!schema.equals("")))
				idDataSchema = Long.valueOf(Long.parseLong(schema.trim()));
			// System.out.println("idDataSchema=" + idDataSchema);
			/*
			 * List<ListDataSource> listDataSources =
			 * listdatasourcedao.getListDataSource(idDataSchema);
			 * System.out.println("idData=" + listDataSources.get(0).getIdData());
			 */
			// getting connection details
			// ListDataSchema listdataDetails =
			// schemadao.getSchemaDetailsForConnectionUtil(idDataSchema);
			/*
			 * String schemaNameForDs = listdataDetails.getSchemaName(); String
			 * schemaTypeForDS = listdataDetails.getSchemaType(); String ipAddressForDS =
			 * listdataDetails.getIpAddress(); String userNameForDS =
			 * listdataDetails.getUsername(); String passwordForDS =
			 * listdataDetails.getPassword();
			 * 
			 * System.out.println("schemaNameForDs =>"+schemaNameForDs);
			 * System.out.println("schemaTypeForDS =>"+schemaTypeForDS);
			 * System.out.println("ipAddressForDS =>"+ipAddressForDS);
			 * System.out.println("userNameForDS =>"+userNameForDS);
			 * System.out.println("passwordForDS =>"+passwordForDS);
			 */

			// JdbcTemplate jdbcTemplateForImport =
			// connectionUtil.getJDBCTemplate(idDataSchema);

			String datalocation = request.getParameter("location");
			System.out.println("datalocation=" + datalocation);
			String importIntoExistingDC = request.getParameter("connectionsourceid");
			if (importIntoExistingDC == null || importIntoExistingDC == " ")
				importIntoExistingDC = "N";
			System.out.println("importIntoExistingDC=" + importIntoExistingDC);
			boolean res1 = true;
			// boolean res1 =
			// importUtility.importSelectedData(file.getOriginalFilename(),jdbcTemplateForImport,projectId);
			System.out.println("file.getOriginalFilename()" + file.getOriginalFilename());
			res1 = importUtility.importSelectedDataDirect(file.getOriginalFilename(), projectId, importIntoExistingDC,
					idDataSchema,file, domainId);

			System.out.println("----- importUtility.importSelectedData res-------" + res1);
			ModelAndView modelAndView = new ModelAndView("ImportSuccess");
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "DTView");

			if (res1)
				// modelAndView.setViewName("ImportSuccess");
				modelAndView.addObject("message", "Imported Successfully");
			else
				modelAndView.addObject("message", "Problem in importing File");

			/*
			 * modelAndView.setViewName("importUI");
			 * modelAndView.addObject("currentSection", "User Settings");
			 * modelAndView.addObject("currentLink", "importAndExport");
			 */

			return modelAndView;
		}

		return new ModelAndView("loginPage");

	}

	@RequestMapping(value = "/getExportUI", method = RequestMethod.GET)
	public ModelAndView getExportUiView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "C", session);
		if (rbac) {
			Long projectId = (Long) session.getAttribute("projectId");
			List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchema(projectId, projList, "", "");
			System.out.println("listdataschema=" + listdataschema);
			ModelAndView modelAndView = new ModelAndView("exportUI");
			modelAndView.addObject("listdataschema", listdataschema);
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "exportUI");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

	/*
	 * @RequestMapping(value = "/ImportSuccess", method = RequestMethod.GET) public
	 * ModelAndView importSuccess(HttpSession session) { Object user =
	 * session.getAttribute("user"); System.out.println("user:" + user); if ((user
	 * == null) || (!user.equals("validUser"))) { return new
	 * ModelAndView("loginPage"); } boolean rbac =
	 * RBACController.rbac("User Settings", "C", session); if (rbac) {
	 * 
	 * ModelAndView modelAndView = new ModelAndView("ImportSuccess");
	 * modelAndView.addObject("currentSection", "User Settings");
	 * modelAndView.addObject("currentLink", "ImportSuccess");
	 * 
	 * return modelAndView; } else return new ModelAndView("ImportSuccess"); }
	 */
	@RequestMapping(value = "/ExportSuccess", method = RequestMethod.GET)
	public ModelAndView exportSuccess(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = RBACController.rbac("User Settings", "C", session);
		if (rbac) {

			ModelAndView modelAndView = new ModelAndView("ExportSuccess");
			modelAndView.addObject("currentSection", "User Settings");
			modelAndView.addObject("currentLink", "ExportSuccess");

			return modelAndView;
		} else
			return new ModelAndView("ExportSuccess");
	}

	@RequestMapping(value = "/exportDataTemplateView", method = RequestMethod.GET)
	public ModelAndView getListDataSourceForExport(HttpServletRequest request, ModelAndView model, HttpSession session)
			throws IOException {
		Object user = session.getAttribute("user");
		// System.out.println("user:" + user);
		/*
		 * String idData = request.getParameter("idData"); String templateName =
		 * request.getParameter("templateName");
		 */
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Data Template", "R", session);
		if (rbac) {
			List<ListDataSource> listdatasource = listdatasourcedao.getListDataSourceTableForExport();
			// listdatasourcedao.getTableNameFromListDataAccess(listdatasource.get(0).getIdData());
			model.addObject("listdatasource", listdatasource);
			model.setViewName("exportDataTemplateView");
			model.addObject("currentSection", "Data Template");
			model.addObject("currentLink", "DTView");
			return model;
		} else
			return new ModelAndView("loginPage");
	}
	/*------------ End of  Changes for Export 15Jan2019 priyanka-----------------*/

	/*-- ----- Changes for export 14jan2019 priyanka ---------  */

	@RequestMapping(value = "/exportDataConnectionView")
	public ModelAndView getListDataSchemaForExport(ModelAndView model, HttpSession session, HttpServletRequest req)
			throws IOException {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		boolean rbac = rbacController.rbac("Data Connection", "R", session);
		if (rbac) {

			List<ListDataSchema> listdataschema = listdatasourcedao.getListDataSchemaForExport();
			model.addObject("listdataschema", listdataschema);

			model.addObject("chkIdDataSchema", session.getAttribute("chkIdDataSchema"));

			model.addObject("currentSection", "Data Connection");
			model.addObject("currentLink", "DCView");
			model.setViewName("exportDataConnectionView");
			return model;
		} else
			return new ModelAndView("loginPage");
	}

	/*--------- -----End of Changes for export 14jan2019 priyanka --------------*/

	/*--------- Changes for export 17jan2019  -----*/
	@RequestMapping(value = "/exportValidationCheck")
	public ModelAndView exportValidationCheckView(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Validation Check", "R", session);
		if (rbac) {

			System.out.println("conrtoller for exportValidationCheck_View");
			List listappslistds = validationcheckdao.getdatafromlistappsandlistdatasourcesForExport();

			System.out.println(listappslistds);

			ModelAndView modelAndView = new ModelAndView("exportValidationCheck");
			modelAndView.addObject("listappslistds", listappslistds);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			return modelAndView;
		} else
			return new ModelAndView("loginPage");
	}

}
