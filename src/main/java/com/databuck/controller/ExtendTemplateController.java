package com.databuck.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.DeleteTempView;
import com.databuck.bean.Domain;
import com.databuck.bean.ListDataDefinition;
import com.databuck.bean.ListDataSource;
import com.databuck.bean.Project;
import com.databuck.bean.TemplateView;
import com.databuck.bean.listDataAccess;
import com.databuck.bean.listDataBlend;
import com.databuck.dao.GlobalRuleDAO;
import com.databuck.dao.ITemplateViewDAO;
import com.databuck.dao.IUserDAO;
import com.databuck.service.RBACController;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.google.common.collect.Multimap;

@Controller
public class ExtendTemplateController {
	@Autowired
	ITemplateViewDAO templateviewdao;
	@Autowired
	private RBACController rbacController;
	@Autowired
	IUserDAO userDAO;
	
	@Autowired
	GlobalRuleDAO globalruledao;
	
	@Autowired
	private Properties appDbConnectionProperties;

	public void setTemplateviewdao(ITemplateViewDAO templateviewdao) {
		this.templateviewdao = templateviewdao;
	}

	@RequestMapping(value = "/extendTemplateView")
	public ModelAndView getListDataSource(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "R",session);
		if(rbac){
		Long projectId = (Long) session.getAttribute("projectId");
		List<Project> projList = (List<Project>) session.getAttribute("userProjectList");
		/*List<TemplateView> templateview = templateviewdao.getTemplateView(projList);*/

		model.addObject("SelectedProjectId",projectId);
		// here set view name which page
		model.addObject("currentSection", "Extend Template");
		model.addObject("currentLink", "ETView");
		model.setViewName("extendTemplateView");

		return model;
		}else
			return new ModelAndView("loginPage");
	}

	

	/*
	 * @RequestMapping(value = "/nonNullyes") public ModelAndView
	 * nullcountyes(HttpServletRequest request) throws IOException { Long
	 * idDataSchema=Long.parseLong(request.getParameter("id"));
	 * 
	 * int idData = Integer.parseInt(request.getParameter("idData"));
	 * System.out.println("idDataSchema+nullcountyes"+idDataSchema);
	 * System.out.println("idData"+idData); int nonNullyes =
	 * templateviewdao.nonNullyes(idData);
	 * System.out.println("nonNullyes"+nonNullyes);
	 * 
	 * 
	 * List<ListDataDefinition> listdatadefinition =
	 * templateviewdao.view(idData);
	 * 
	 * System.out.println(listdatadefinition.size()); ModelAndView model = new
	 * ModelAndView("listDataView");
	 * model.addObject("idDataSchema",listdatadefinition.get(0).getIdDataSchema(
	 * )); model.addObject("idData",idData);
	 * model.addObject("listdatadefinition", listdatadefinition);
	 * model.addObject("currentSection","Validation Check");
	 * model.addObject("currentLink","Add New"); return model; }
	 */

	/*
	 * @RequestMapping(value = "/primaryKeyyes") public ModelAndView
	 * primaryKeyyes(HttpServletRequest req,HttpServletResponse resp) throws
	 * IOException { Long idDataSchema=Long.parseLong(req.getParameter("id"));
	 * 
	 * int idData = Integer.parseInt(req.getParameter("idData"));
	 * System.out.println("idDataSchema+nullcountyes"+idDataSchema);
	 * System.out.println("idData"+idData); int primaryKeyyes =
	 * templateviewdao.primaryKeyyes(idData);
	 * System.out.println("primaryKeyyes"+primaryKeyyes);
	 * 
	 * 
	 * List<ListDataDefinition> listdatadefinition =
	 * templateviewdao.view(idData);
	 * 
	 * System.out.println(listdatadefinition.size()); ModelAndView model = new
	 * ModelAndView("listDataView");
	 * model.addObject("idDataSchema",listdatadefinition.get(0).getIdDataSchema(
	 * )); model.addObject("idData",idData);
	 * model.addObject("currentSection","Validation Check");
	 * model.addObject("currentLink","Add New");
	 * model.addObject("listdatadefinition", listdatadefinition); return model;
	 * }
	 * 
	 * @RequestMapping(value = "/hashValueyes") public ModelAndView
	 * hashValueyes(HttpServletRequest req) throws IOException {
	 * 
	 * 
	 * int idData = Integer.parseInt(req.getParameter("idData"));
	 * 
	 * System.out.println("idData"+idData); int nullcountyes =
	 * templateviewdao.hashValueyes(idData);
	 * System.out.println("hashValueyes"+nullcountyes);
	 * 
	 * 
	 * List<ListDataDefinition> listdatadefinition =
	 * templateviewdao.view(idData);
	 * 
	 * System.out.println(listdatadefinition.size()); ModelAndView model = new
	 * ModelAndView("listDataView");
	 * model.addObject("idDataSchema",listdatadefinition.get(0).getIdDataSchema(
	 * )); model.addObject("idData",idData);
	 * model.addObject("currentSection","Validation Check");
	 * model.addObject("currentLink","Add New");
	 * model.addObject("listdatadefinition", listdatadefinition); return model;
	 * }
	 * 
	 * @RequestMapping(value = "/numericalStatyes") public ModelAndView
	 * numericalStatyes(HttpServletRequest req) throws IOException {
	 * 
	 * 
	 * int idData = Integer.parseInt(req.getParameter("idData"));
	 * 
	 * System.out.println("idData"+idData); int nullcountyes =
	 * templateviewdao.numericalStatyes(idData);
	 * System.out.println("numericalStatyes"+nullcountyes);
	 * 
	 * 
	 * List<ListDataDefinition> listdatadefinition =
	 * templateviewdao.view(idData);
	 * 
	 * System.out.println(listdatadefinition.size()); ModelAndView model = new
	 * ModelAndView("listDataView");
	 * model.addObject("idDataSchema",listdatadefinition.get(0).getIdDataSchema(
	 * )); model.addObject("idData",idData);
	 * model.addObject("currentSection","Validation Check");
	 * model.addObject("currentLink","Add New");
	 * model.addObject("listdatadefinition", listdatadefinition); return model;
	 * }
	 * 
	 * @RequestMapping(value = "/stringStatyes") public ModelAndView
	 * stringStatyes(HttpServletRequest req) throws IOException {
	 * 
	 * 
	 * int idData = Integer.parseInt(req.getParameter("idData"));
	 * 
	 * System.out.println("idData"+idData); int nullcountyes =
	 * templateviewdao.stringStatyes(idData);
	 * System.out.println("stringStatyes"+nullcountyes);
	 * 
	 * 
	 * List<ListDataDefinition> listdatadefinition =
	 * templateviewdao.view(idData);
	 * 
	 * System.out.println(listdatadefinition.size()); ModelAndView model = new
	 * ModelAndView("listDataView");
	 * model.addObject("idDataSchema",listdatadefinition.get(0).getIdDataSchema(
	 * )); model.addObject("idData",idData);
	 * model.addObject("currentSection","Validation Check");
	 * model.addObject("currentLink","Add New");
	 * model.addObject("listdatadefinition", listdatadefinition); return model;
	 * }
	 */
	/**
	 * 
	 * 
	 * Delete the Extend Template
	 * 
	 * 
	 */

	@RequestMapping(value = "/deletederivedcolumns", method = RequestMethod.POST)
	public ModelAndView deletederivedcolumns(HttpServletResponse response, HttpSession session,
			@RequestParam Long idDataBlend) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "D",session);
		if(rbac){
		
		System.out.println("idDataBlend=" + idDataBlend);
		int deletederivedcolumns = templateviewdao.deletederivedcolumns(idDataBlend);
		System.out.println("deletederivedcolumns=" + deletederivedcolumns);
		if (deletederivedcolumns > 0) {
			try {
				response.getWriter().println("Deleted successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
		}else
			return new ModelAndView("loginPage");

	}

	@RequestMapping(value = "/deletefilter", method = RequestMethod.POST)
	public ModelAndView deletefilter(HttpServletResponse response, HttpSession session,
			@RequestParam Long idDataBlend) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		System.out.println("idDataBlend=" + idDataBlend);
		int deletefilter = templateviewdao.deletefilter(idDataBlend);
		System.out.println("deletefilter=" + deletefilter);
		if (deletefilter > 0) {
			try {
				response.getWriter().println("Deleted successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	@RequestMapping(value = "/deleteTemp", method = RequestMethod.GET)
	public ModelAndView editTemp(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "D",session);
		if(rbac){
		
		long idDataBlend = Integer.parseInt(request.getParameter("idDataBlend"));
		System.out.println("delete extend view id " + idDataBlend);

		/*int idData = Integer.parseInt(request.getParameter("idData").trim());
		String lsdescription = request.getParameter(("lsdescription"));
		String lbdescription = request.getParameter(("lbdescription"));
		String name = request.getParameter(("name"));
		System.out.println("name=" + name);
		System.out.println("lbdescription=" + lbdescription);
		System.out.println("lsdescription=" + lsdescription);
		System.out.println("idData=" + idData);*/

		  templateviewdao.deleteDataFromListDataBlend(idDataBlend);
		  ModelAndView modelandview = new ModelAndView("updateTaskSuccess");
		  modelandview.addObject("msg", "Extend Template Deleted Successfully");
		  modelandview.addObject("currentSection", "Extend Template");
			modelandview.addObject("currentLink", "ETView");
		/*ModelAndView modelandview = new ModelAndView("deleteExtendTemplate");

		List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
		System.out.println("listdatadefinition=" + listdatadefinition);
		Object[] derievedColumns = templateviewdao.getDerievedColumns(idData);
		Map mapobjectFilters = (Map) derievedColumns[1];
		Map mapobjectderievedColumns = (Map) derievedColumns[2];
		System.out.println("mapobjectderievedColumns" + mapobjectderievedColumns);
		System.out.println("mapobjectFilters" + mapobjectFilters);

		modelandview.addObject("mapobjectFilters", mapobjectFilters);
		modelandview.addObject("mapobjectderievedColumns", mapobjectderievedColumns);
		modelandview.addObject("name", name);
		modelandview.addObject("idDataBlend", idDataBlend);
		modelandview.addObject("idData", idData);
		modelandview.addObject("lbdescription", lbdescription);
		modelandview.addObject("lsdescription", lsdescription);
		// modelandview.addObject("deletetempview", deletetempview);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "Add New");*/
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	/**
	 * Delete Extend Template completely
	 * 
	 * 
	 */
	@RequestMapping(value = "/deleteTempId", method = RequestMethod.POST)
	public ModelAndView DeleteTempView(HttpServletResponse response, HttpServletRequest request, HttpSession session,
			@RequestParam Long idDataBlend) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		System.out.println("idDataBlend=" + idDataBlend);
		int count = templateviewdao.DeleteTempViewFully(idDataBlend);
		System.out.println("count=" + count);
		ModelAndView model = new ModelAndView("deleteExtendTemplate");
		model.addObject("currentSection", "Extend Template");
		model.addObject("currentLink", "ETAdd New");

		if (count > 0) {
			try {
				response.getWriter().println("Deleted Extended Template Successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			model.addObject("message", "Deleted Extended Template Successfully");
			return model;
		} else {
			model.addObject("message", "Unable to Delete Extended Template");
			return model;
		}
	}

	/**
	 * Delete Extend Template list DataBaseColDEfination
	 * 
	 * 
	 * 
	 */
	@RequestMapping(value = "/deletelistdata", method = RequestMethod.GET)
	public ModelAndView DeleteTempViewListBeanCol(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		int count = 0;

		System.out.println("requesDeleteTempViewListBeanCol  id= " + request.getParameter("idDataBlend"));

		int idDataBlend = Integer.parseInt(request.getParameter("idDataBlend"));

		count = templateviewdao.DeleteListDataBeanColDef(idDataBlend);

		System.out.println(" DeleteListDataBeanColDef  count print  " + count);
		ModelAndView model = new ModelAndView();
		model.addObject("currentSection", "Extend Template");
		model.addObject("currentLink", "ETAdd New");
		if (count > 0) {
			model.addObject("deleteViewListColDef");
			return model;
		} else {
			model.addObject("deleteTemplate");
			return model;
		}
	}

	/**
	 * 
	 * 
	 * this is for add/drived coulumn filter
	 * 
	 * @return
	 */

	@RequestMapping(value = "/createcolumn", method = RequestMethod.GET)
	public ModelAndView getCreateTemplateView(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		if(rbac){
		
		int idDataBlend = Integer.parseInt(request.getParameter("idDataBlend").trim());
		Long idData = Long.parseLong(request.getParameter("idData").trim());
		String lsdescription = request.getParameter(("lsdescription"));
		String lbdescription = request.getParameter(("lbdescription"));
		String name = request.getParameter(("name"));
		System.out.println("name=" + name);
		System.out.println("lbdescription=" + lbdescription);
		System.out.println("lsdescription=" + lsdescription);
		System.out.println(idDataBlend);
		System.out.println(idData);
		List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
		ModelAndView modelandview = new ModelAndView("createColumn");
		Long projectId= (Long)session.getAttribute("projectId");
		List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
		modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
		
		modelandview.addObject("listdatadefinition", listdatadefinition);
		modelandview.addObject("name", name);
		modelandview.addObject("idDataBlend", idDataBlend);
		modelandview.addObject("idData", idData);
		modelandview.addObject("lbdescription", lbdescription);
	    modelandview.addObject("lsdescription", lsdescription);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "ETView");
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/saveDataInListDataBlendTables", method = RequestMethod.POST)
	public void saveDataInListDataBlendTables(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam String extensionType, @RequestParam String derivedColumnName,
			@RequestParam String columnCategory, @RequestParam String columnValueType,
			@RequestParam long listDataDefinitionIdCol, @RequestParam String expression,
			@RequestParam String columnValue, @RequestParam Long idDataBlend, @RequestParam Long idData,
			@RequestParam String filterName,@RequestParam String name,@RequestParam String description,@RequestParam String rowAdd) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		if ((user == null) || (!user.equals("validUser"))) {
			try {
				response.sendRedirect("loginPage.jsp");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("rowAdd="+rowAdd);
		System.out.println("extensionType=" + extensionType);
		System.out.println("derivedColumnName=" + derivedColumnName);
		System.out.println("columnCategory=" + columnCategory);
		// System.out.println("selectid="+selectid);
		System.out.println("columnValueType=" + columnValueType);
		System.out.println("listDataDefinitionIdCol=" + listDataDefinitionIdCol);
		System.out.println("expression=" + expression);
		System.out.println("columnValue=" + columnValue);
		System.out.println("idDataBlend=" + idDataBlend);
		System.out.println("idData=" + idData);
		System.out.println("filterName=" + filterName);
		System.out.println("name=" + name);
		System.out.println("description=" + description);
		
		listDataBlend ldb = new listDataBlend();
		ldb.setColumnName(derivedColumnName);
		ldb.setColumnValueType(columnValueType);
		//ldb.setIdColumn(listDataDefinitionIdCol);
		ldb.setExpression(expression);
		ldb.setIdData(idData);
		ldb.setColumnValue(columnValue);
		ldb.setDerivedColType(columnCategory);
		ldb.setFilterName(filterName);
		ldb.setIdDataBlend(idDataBlend);
		ldb.setName(name);
		ldb.setDescription(description);
		ldb.setIdUser(idUser);
		ldb.setIdColumn(listDataDefinitionIdCol);
		
		if(!(extensionType.equalsIgnoreCase("Row Add"))){
		if (columnCategory.equals("-1")) { // -1=matching
			if (extensionType.equals("Derived Column")) {
				System.out.println("Matching Derieved Column");
				templateviewdao.matchingDerivedColumn(ldb);
			} else if (extensionType.equals("Filter")) {
				System.out.println("Matching Filter");
				templateviewdao.matchingFilter(ldb);
			}
		}
		if (!(columnCategory.equals("-1"))) { //Quality
			System.out.println("Quality");
			templateviewdao.qualityApplication(ldb);
		}
		}else{
			//RowAdd
			String substring = rowAdd.substring(0,rowAdd.length()-1);
			ldb.setRowAddExpression(substring);
			templateviewdao.insertIntoListDataBlendRowAdd(ldb);
		}
		/*
		 * // int i = templateviewdao.savedataforderievedcolumns(name, formula,
		 * // blendid, sourceid, //
		 * blendcolumn,columnCategory,columnValue,columnValueType); } else { //
		 * templateviewdao.savedataforfilter(formulaname, formula, blendid, //
		 * sourceid, blendcolumn);
		 */
		ModelAndView modelandview = new ModelAndView("createColumn");
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "ETAdd New");

		// return modelandview;
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println("success");
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "R",session);
		if(rbac){
		
		int idDataBlend = Integer.parseInt(request.getParameter("idDataBlend").trim());
		Long idData = Long.parseLong(request.getParameter("idData").trim());
		String lsdescription = request.getParameter(("lsdescription"));
		String lbdescription = request.getParameter(("lbdescription"));
		String name = request.getParameter(("name"));
		System.out.println("name=" + name);
		System.out.println("lbdescription=" + lbdescription);
		System.out.println("lsdescription=" + lsdescription);
		System.out.println(idDataBlend);
		System.out.println(idData);
		ModelAndView modelandview = new ModelAndView("dataBlendDisplayView");
		List<ListDataDefinition> listdatadefinition = templateviewdao.view(idData);
		System.out.println("listdatadefinition=" + listdatadefinition);
		Object[] derievedColumns = templateviewdao.getDerievedColumns(idData);
		List<listDataBlend> listDataBlend = templateviewdao.getDataFromListDataBlend(name);
		modelandview.addObject("listDataBlend", listDataBlend);
		
		Map mapobjectFilters = (Map) derievedColumns[1];
		//Multimap<String,String> mapobjectderievedColumns = (Multimap<String, String>) derievedColumns[2];
		//System.out.println("mapobjectderievedColumns" + mapobjectderievedColumns);
		
		System.out.println("mapobjectFilters" + mapobjectFilters);
		
		modelandview.addObject("listdatadefinition", listdatadefinition);
		//modelandview.addObject("triggerData", mapobjectderievedColumns);
		modelandview.addObject("mapobjectFilters", mapobjectFilters);
		modelandview.addObject("name", name);
		modelandview.addObject("lbdescription", lbdescription);
		modelandview.addObject("lsdescription", lsdescription);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "ETView");
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/addNewExtendTemplate", method = RequestMethod.GET)
	public ModelAndView addNewExtendTemplate(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		if(rbac){
			Long projectId= (Long)session.getAttribute("projectId");
		List<ListDataSource> getlistdatasourcesname = templateviewdao.getlistdatasourcesname(projectId);
		List<Domain> listdomain = globalruledao.getDomainList();
		
		ModelAndView modelandview = new ModelAndView("dataBlendCreateView");
		modelandview.addObject("getlistdatasourcesname", getlistdatasourcesname);
		modelandview.addObject("listdomain", listdomain);
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "ETAdd New");
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}

	@RequestMapping(value = "/createExtendTemplate", method = RequestMethod.POST)
	public ModelAndView createExtendTemplate(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, @RequestParam Long sourceid, @RequestParam String name,
			@RequestParam String description) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		long idUser = (Long) session.getAttribute("idUser");
		System.out.println("idUser=" + idUser);
		Long projectId= (Long)session.getAttribute("projectId");

		
		// activedirectory flag check
					String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
					System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
					String createdByUser="";
					if(activeDirectoryFlag.equalsIgnoreCase("Y"))
					{
					 	createdByUser=(String) session.getAttribute("createdByUser");
						System.out.println("======= createdByUser in extended template ===>"+createdByUser);
					}else {
						// getting createdBy username from createdBy userId
						System.out.println("======= idUser ===>"+idUser);

						createdByUser = userDAO.getUserNameByUserId(idUser);

						System.out.println("======= createdByUser in extended template ===>"+createdByUser);
					}

		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		boolean rbac = rbacController.rbac("Extend Template & Rule", "C",session);
		if(rbac){
		
		System.out.println("name=" + name);
		System.out.println("sourceid=" + sourceid);
		System.out.println("description=" + description);
		templateviewdao.insertintolistdatablend(name, sourceid, description, idUser, createdByUser,projectId);
		ModelAndView modelandview = new ModelAndView("dataBlendCreateView");
		modelandview.addObject("currentSection", "Extend Template");
		modelandview.addObject("currentLink", "ETAdd New");
		try {
			response.getWriter().println("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modelandview;
		}else
			return new ModelAndView("loginPage");
	}
}
