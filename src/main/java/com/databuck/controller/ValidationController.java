package com.databuck.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.ListApplications;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSchema;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.TemplateDeltaResponse;
import com.databuck.dao.IListDataSourceDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.SchemaDAOI;
import com.databuck.service.DataTemplateDeltaCheckService;
import com.databuck.service.IValidationService;

@Controller
public class ValidationController {

	@Autowired
	public SchemaDAOI SchemaDAOI;

	@Autowired
	public ITemplateViewDAO templateviewdao;

	@Autowired
	public IListDataSourceDAO listdatasourcedao;

	@Autowired
	public IValidationService validationService;

	@Autowired
	private DataTemplateDeltaCheckService dataTemplateDeltaCheckService;

	String decrypt = null;

	@RequestMapping(value = "/saveAllAndIdentityThreashold", method = RequestMethod.POST)
	public ModelAndView saveSchemaValues(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("showFormResults");
		try {
			String sIdApp = request.getParameter("idApp");
			long threshold = Long.parseLong(request.getParameter("threshold"));
			long identity = Long.parseLong(request.getParameter("identity"));
			long idApp = Long.parseLong(sIdApp);
			int[] updatedQueries = validationService.saveSchemavalues(threshold, identity, idApp);
			System.out.println("updatedQueries:" + updatedQueries);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "VCView");
			modelAndView.addObject("updatestatus", "updated values in  database  succesufully");
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.addObject("updatestatus", "problem while updating values");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/deleteApplication")
	public ModelAndView deleteValidationCheckView(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		System.out.println("in con:" + request.getAttribute("listOfListApplicationsids"));
		long idApp = Long.parseLong(request.getParameter("idApp"));
		String name = request.getParameter("name");
		ModelAndView modelAndView = new ModelAndView("confirmDelete");
		modelAndView.addObject("idApp", idApp);
		modelAndView.addObject("name", name);
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;
	}

	@RequestMapping(value = "/deleteApplicationInView")
	public ModelAndView confirmDeletionInValidationCheckView(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("validationView");
		long idApp = Long.parseLong(request.getParameter("idApp"));
		String selected = request.getParameter("selected");
		if (selected.equalsIgnoreCase("yes")) {
			boolean deleteStatus = validationService.deleteValidationViewApplication(idApp);
			String delete = "";
			if (deleteStatus)
				delete = "Successfully deleted.";
			else
				delete = "Problem while deleting.";
			modelAndView.addObject("delete", delete);
		}

		// List<ListApplications> listOfListApplicationsObjs=(List<ListApplications>
		// )request.getParameter("listOfListApplicationsObjs");
		List<ListApplications> listApplicationDataOfApps = validationService.validationCheckView();
		modelAndView.addObject("listApplicationDataOfApps", listApplicationDataOfApps);
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;

	} //

	@RequestMapping(value = "/showDupThresholdForm", method = RequestMethod.GET)
	public ModelAndView showDupThresholdForm(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("showDupThresholdForm");
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;
	}

	@RequestMapping(value = "/viewSources", method = RequestMethod.GET)
	public ModelAndView viewlistDataSourcesOfIdApp(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("viewListDataSources");
		long idData = Long.parseLong(request.getParameter("idData"));
		ListDataSource listDataSourceobj = validationService.getListDataSourceDataOfIdData(idData);
		modelAndView.addObject("listDataSourceobj", listDataSourceobj);
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;
	}

	@RequestMapping(value = "/viewMetaData", method = RequestMethod.GET)
	public ModelAndView getMetaData(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		List<ListDataDefinition> listDataDefinitionData = validationService.getListDataDefinitionData(idData);
		modelAndView.addObject("listDataDefinitionData", listDataDefinitionData);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("currentSection", "Validation Check");
		modelAndView.addObject("currentLink", "VCView");
		return modelAndView;
	}

	@RequestMapping(value = "/nonNullyes", method = RequestMethod.GET)
	public ModelAndView changeAllNonNullsToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName nonNullyes ==========================" + templateName);

		boolean status = validationService.changeAllNonNullsToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated succuss fully");
		else
			modelAndView.addObject("status", "problem while updating notNulls");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		ModelAndView model = new ModelAndView("listDataView");
		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/primaryKeyyes")
	public ModelAndView primaryKeyyes(HttpServletRequest req, HttpServletResponse response, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idData = Long.parseLong(req.getParameter("idData"));
		System.out.println("idData" + idData);
		String templateName = req.getParameter("name");
		System.out.println("templateName primaryKeyyes ==========================" + templateName);
		int primaryKeyyes = templateviewdao.primaryKeyyes(idData);
		System.out.println("primaryKeyyes" + primaryKeyyes);

		System.out.println("templateName primaryKeyyes ==========================" + templateName);

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		ModelAndView model = new ModelAndView("listDataView");
		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		model.addObject("idData", idData);
		model.addObject("name", templateName);
		model.addObject("currentSection", "Data Template");
		model.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/hashValueyes")
	public ModelAndView hashValueyes(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idData = Long.parseLong(request.getParameter("idData"));
		System.out.println("idData" + idData);
		String templateName = request.getParameter("name");
		System.out.println("templateName hashValueyes ==========================" + templateName);
		int nullcountyes = templateviewdao.hashValueyes(idData);
		System.out.println("hashValueyes" + nullcountyes);

		ModelAndView model = new ModelAndView("listDataView");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		model.addObject("idData", idData);
		model.addObject("name", templateName);
		model.addObject("currentSection", "Data Template");
		model.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/numericalStatyes")
	public ModelAndView numericalStatyes(HttpServletRequest req, HttpSession session, HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		Long idData = Long.parseLong(req.getParameter("idData"));

		System.out.println("idData" + idData);
		int nullcountyes = templateviewdao.numericalStatyes(idData);
		System.out.println("numericalStatyes" + nullcountyes);

		String templateName = req.getParameter("name");
		System.out.println("templateName numericalStatyes ==========================" + templateName);

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		ModelAndView model = new ModelAndView("listDataView");
		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		model.addObject("idData", idData);
		model.addObject("name", templateName);
		model.addObject("currentSection", "Data Template");
		model.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/stringStatyes")
	public ModelAndView stringStatyes(HttpServletRequest req, HttpSession session, HttpServletResponse response) {

		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idData = Long.parseLong(req.getParameter("idData"));

		System.out.println("idData" + idData);
		int nullcountyes = templateviewdao.stringStatyes(idData);
		System.out.println("stringStatyes" + nullcountyes);

		String templateName = req.getParameter("name");
		System.out.println("templateName stringStatyes ==========================" + templateName);

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		ModelAndView model = new ModelAndView("listDataView");
		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		model.addObject("idData", idData);
		model.addObject("name", templateName);
		model.addObject("currentSection", "Data Template");
		model.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/dupkeyyes")
	public ModelAndView dupkeyyes(HttpServletRequest req, HttpSession session, HttpServletResponse response) {
		Object user = session.getAttribute("user");
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idData = Long.parseLong(req.getParameter("idData"));
		System.out.println("idData=" + idData);
		int updateDupKeyYes = templateviewdao.updateDupKeyYes(idData);
		System.out.println("updateDupKeyYes=" + updateDupKeyYes);

		String templateName = req.getParameter("name");
		System.out.println("templateName dupkeyyes ==========================" + templateName);

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		ModelAndView model = new ModelAndView("listDataView");
		model.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		model.addObject("templateDeltaResponse", templateDeltaResponse);
		model.addObject("idData", idData);
		model.addObject("name", templateName);
		model.addObject("currentSection", "Data Template");
		model.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;

	}

	@RequestMapping(value = "/validationvalidationCheck_AddNew_Create", method = RequestMethod.GET)
	public ModelAndView createSchemainVC(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		Long idUser = (Long) session.getAttribute("idUser");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idDataSchema = Long.parseLong(req.getParameter("idDataSchema"));
		// System.out.println("idDataSchema"+idDataSchema);
		List<ListDataSchema> listDataSchema = SchemaDAOI.readdatafromlistdataschema(idDataSchema);
		ListDataSchema ListDataSchemaobj = (ListDataSchema) listDataSchema.get(0);
		// System.out.println("new :"+ListDataSchemaobj.getIpAddress());
		String uri = ListDataSchemaobj.getIpAddress();
		String database = ListDataSchemaobj.getDatabaseSchema();
		String username = ListDataSchemaobj.getUsername();
		String password = ListDataSchemaobj.getPassword();
		String port = ListDataSchemaobj.getPort();
		String results = "";
		// Long idDataSchema = SchemaDAOI.savedata(uri, database, username, password,
		// port);
		ModelAndView modelAndView = new ModelAndView("demo");
		Object[] readTablesFromVertica = SchemaDAOI.readTablesFromVertica(uri, database, username, password, port,
				idDataSchema, idUser);
		// tableList,listDataSourcesIds

		List tableList = (List) readTablesFromVertica[1];
		Long[] listDataSourcesIds = (Long[]) readTablesFromVertica[2];
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(cal.getTime());

		List<Long> listApplicationsIdList = SchemaDAOI.insertDataInListApplications(tableList, listDataSourcesIds,
				database, strDate, idUser);
		Long[] listDfSetRuleidDfDfSetArray = SchemaDAOI.insertListDFSetRule(listApplicationsIdList);
		SchemaDAOI.insertIntoTranRule(listApplicationsIdList);
		// SchemaDAOI.insertintolds(metaDataMap, idDataSchema);
		if ((Boolean) readTablesFromVertica[0] == false) {
			results = "please enter correct details";
			modelAndView.addObject("result", results);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add New");

			return modelAndView;
		} else {
			results = "details saved successfully";
			modelAndView.addObject("result", results);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add New");

			return modelAndView;
		}
	}

	@RequestMapping(value = "/validationCheck_AddNew_Customize", method = RequestMethod.GET)
	public ModelAndView customizeSchemainVC(HttpServletRequest req, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		Long idUser = (Long) session.getAttribute("idUser");
		Long idDataSchema = Long.parseLong(req.getParameter("idDataSchema"));
		// System.out.println("idDataSchema"+idDataSchema);
		List<ListDataSchema> listDataSchema = SchemaDAOI.readdatafromlistdataschema(idDataSchema);
		ListDataSchema ListDataSchemaobj = (ListDataSchema) listDataSchema.get(0);
		// System.out.println("new :"+ListDataSchemaobj.getIpAddress());
		String uri = ListDataSchemaobj.getIpAddress();
		String database = ListDataSchemaobj.getDatabaseSchema();
		String username = ListDataSchemaobj.getUsername();
		String password = ListDataSchemaobj.getPassword();
		//System.out.println("password=" + password);

		// decrypt password

		try {
			// decrypt = encryptionDecryption.decrypt(password);
			System.out.println("decrypt=" + decrypt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String port = ListDataSchemaobj.getPort();
		String results = "";
		// Long idDataSchema = SchemaDAOI.savedata(uri, database, username, password,
		// port);
		ModelAndView modelAndView = new ModelAndView("demo");
		Object[] readTablesFromVertica = SchemaDAOI.readTablesFromVertica(uri, database, username, decrypt, port,
				idDataSchema, idUser);
		List tableList = (List) readTablesFromVertica[1];
		Long[] listDataSourcesIds = (Long[]) readTablesFromVertica[2];
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(cal.getTime());
		List<Long> listApplicationsIdList = SchemaDAOI.insertDataInListApplications(tableList, listDataSourcesIds,
				database, strDate, idUser);
		Long[] listDfSetRuleidDfDfSetArray = SchemaDAOI.insertListDFSetRule(listApplicationsIdList);

		SchemaDAOI.insertIntoTranRule(listApplicationsIdList);
		SchemaDAOI.insertListDFSetComparisonRule(listDfSetRuleidDfDfSetArray, listDataSourcesIds);
		if ((Boolean) readTablesFromVertica[0] == false) {
			results = "please enter correct details";
			modelAndView.addObject("result", results);
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add New");

			return modelAndView;
		} else {
			for (Long id : listApplicationsIdList) {
				System.out.println("idapp in for is:" + id);
			}
			System.out.println("getting data from listapplicationss");
			List<ListApplications> listOfListApplicationsObjs = listdatasourcedao
					.getDataFromListApplicationsforValidationCheckAddNewCustomize(listApplicationsIdList, database);
			System.out.println("getting data from listdatasources");
			List<ListDataSource> listdatasource = listdatasourcedao.getListDataSource(idDataSchema);
			// modelAndView.addObject("listdatasource", listdatasource);
			modelAndView.addObject("listApplicationsIdList", listApplicationsIdList);
			modelAndView.addObject("listOfListApplicationsObjs", listOfListApplicationsObjs);
			// here set view name whicj page
			modelAndView.addObject("currentSection", "Validation Check");
			modelAndView.addObject("currentLink", "Add New");

			modelAndView.setViewName("validationView");
			return modelAndView;
		}
	}

	@RequestMapping(value = "/microSegmentyes", method = RequestMethod.GET)
	public ModelAndView changeAllMicrosegmentToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName microsegment ==========================" + templateName);

		boolean status = validationService.changeAllMicrosegmentToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/lastReadTimeyes", method = RequestMethod.GET)
	public ModelAndView changeAlllastReadTimeToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName last read time ==========================" + templateName);

		boolean status = validationService.changeAlllastReadTimeToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/doNotDisplayyes", method = RequestMethod.GET)
	public ModelAndView changeAllIsMaskedToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName do not display ==========================" + templateName);

		boolean status = validationService.changeAllIsMaskedToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/partitionByyes", method = RequestMethod.GET)
	public ModelAndView changeAllPartitionByToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName partition by ==========================" + templateName);

		boolean status = validationService.changeAllPartitionByToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/dataDriftyes", method = RequestMethod.GET)
	public ModelAndView changeAllDataDriftToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName DataDrift ==========================" + templateName);

		boolean status = validationService.changeAllDataDriftToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/startDateyes", method = RequestMethod.GET)
	public ModelAndView changeAllStartDateToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName start date ==========================" + templateName);

		boolean status = validationService.changeAllStartDateToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/endDateyes", method = RequestMethod.GET)
	public ModelAndView changeAllEndDateToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName end date ==========================" + templateName);

		boolean status = validationService.changeAllEndDateToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/timelinessKeyyes", method = RequestMethod.GET)
	public ModelAndView changeAllTimelinessKeyToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName timeliness key ==========================" + templateName);

		boolean status = validationService.changeAllTimelinessKeyToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/recordAnomalyyes", method = RequestMethod.GET)
	public ModelAndView changeAllRecordAnomalyToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName record anomaly ==========================" + templateName);

		boolean status = validationService.changeAllRecordAnomalyToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/defaultCheckyes", method = RequestMethod.GET)
	public ModelAndView changeAllDefaultCheckToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName default check ==========================" + templateName);

		boolean status = validationService.changeAllDefaultCheckToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/dateRuleyes", method = RequestMethod.GET)
	public ModelAndView changeAllDateRuleToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName date rule check ==========================" + templateName);

		boolean status = validationService.changeAllDateRuleToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/patternCheckyes", method = RequestMethod.GET)
	public ModelAndView changeAllPatternCheckToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName pattern check ==========================" + templateName);

		boolean status = validationService.changeAllPatternCheckToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	
	@RequestMapping(value = "/defaultPatternCheckyes", method = RequestMethod.GET)
	public ModelAndView changeAllDefaultPatternCheckToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName default pattern check ==========================" + templateName);

		boolean status = validationService.changeAllDefaultPatternCheckToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/badDatayes", method = RequestMethod.GET)
	public ModelAndView changeAllBadDataToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName bad data check ==========================" + templateName);

		boolean status = validationService.changeAllBadDataToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/lengthCheckyes", method = RequestMethod.GET)
	public ModelAndView changeAllLengthCheckToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName length check ==========================" + templateName);

		boolean status = validationService.changeAllLengthCheckToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/maxLengthCheckyes", method = RequestMethod.GET)
	public ModelAndView changeAllMaxLengthCheckToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName Max length check ==========================" + templateName);

		boolean status = validationService.changeAllMaxLengthCheckToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "/measurementyes", method = RequestMethod.GET)
	public ModelAndView changeAllMatchValuetToYes(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			// return new ModelAndView("loginPage");
		}
		ModelAndView modelAndView = new ModelAndView("listDataView");
		long idData = Long.parseLong(request.getParameter("idData"));
		String templateName = request.getParameter("name");
		System.out.println("templateName Match value ==========================" + templateName);

		boolean status = validationService.changeAllMatchValuetToYes(idData);
		if (status)
			modelAndView.addObject("status", "updated sucess fully");
		else
			modelAndView.addObject("status", "problem while updating microsegment");

		// Delta changes of listDatadefinition
		TemplateDeltaResponse templateDeltaResponse = dataTemplateDeltaCheckService.getTemplateDeltaChanges(idData);

		modelAndView.addObject("listDataDefinitionData", templateDeltaResponse.getDeltaListDataDefinition());
		modelAndView.addObject("templateDeltaResponse", templateDeltaResponse);
		modelAndView.addObject("idData", idData);
		modelAndView.addObject("name", templateName);
		modelAndView.addObject("currentSection", "Data Template");
		modelAndView.addObject("currentLink", "DTView");
		try {
			response.sendRedirect("listdataview?idData=" + idData + "&dataLocation=value" + "&name=" + templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return modelAndView;
	}
}