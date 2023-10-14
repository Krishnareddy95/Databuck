package com.databuck.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.databuck.Migration.Migration;
import com.databuck.bean.User;
import com.databuck.dao.IProjectDAO;
import com.databuck.bean.Domain;
import com.databuck.bean.Project;
import com.databuck.service.IGroupService;
import com.databuck.service.IProjectService;
import com.databuck.service.RBACController;

@Controller
public class ProjectController {
	@Autowired
	private RBACController rbacController;

	@Autowired
	public IProjectService projectService;
	@Autowired
	public IProjectDAO projectDao;
	@Autowired
	public IGroupService groupService;
	@Autowired
	public IProjectService iprojectService ;
	@Autowired
	private Properties appDbConnectionProperties;

	@RequestMapping(value = "/editProject")
	public ModelAndView editProject(HttpServletRequest req, ModelAndView model, HttpSession session) {
		List<User> lstUser = null;
		List<User> lstassignUser = null;
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		long projectId = Long.parseLong(req.getParameter("idApp"));
		Project selectedProject = projectService.getSelectedProject(projectId);
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
		if (activeDirectoryFlag.equals("Y")) {
			lstUser = groupService.getAllGroupsfromActiveDirectory();
			lstassignUser =  groupService.getAllassignGroupsfromActiveDirectory(projectId);
			
			
		} else {
			lstUser = groupService.getAllGroups();
			lstassignUser = groupService.getAllassignGroups(projectId);
			
		}

		model.addObject("groupList", lstUser);
	   model.addObject("assignedgroupList", lstassignUser);
		model.addObject("selectedProject", selectedProject);
		model.setViewName("editProject");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "Add New Project");
		return model;
	}
	
	@RequestMapping(value = "/domainResultView")
	public void domainResultView(HttpServletRequest request, HttpServletResponse response) {

		JSONObject result = new JSONObject();
		String pageNo = request.getParameter("iDisplayStart");
		String pageSize = request.getParameter("iDisplayLength");
		String sSearch = request.getParameter("sSearch");

		int numRecords = 10;
		int start = 0;

		if (pageNo != null) {
			start = Integer.parseInt(pageNo);
			if (start < 0) {
				start = 0;
			}
		}
		if (pageSize != null) {
			numRecords = Integer.parseInt(pageSize);
			if (numRecords < 10 || numRecords > 50) {
				numRecords = 10;
			}
		}

		try {

			List<Domain> paginationProject = projectService.getPaginationDomain(start, numRecords, sSearch);

			JSONArray array = new JSONArray();
			for (Iterator<Domain> lstIterator = paginationProject.iterator(); lstIterator.hasNext();) {
				Domain dataQltDashboard = lstIterator.next();
				JSONArray ja = new JSONArray();
				ja.put(dataQltDashboard.getDomainId());
				ja.put(dataQltDashboard.getDomainName());
				//ja.put(dataQltDashboard.getProjectDescription());
				ja.put("<a href='editDomain?idApp=" + dataQltDashboard.getDomainId()
						+ "' data-toggle='confirmation'><span class='fa fa-edit'></span></a>");
				ja.put("<a href='deleteDomaint?id=" + dataQltDashboard.getDomainId()
						+ "'  data-toggle='confirmation'><span style='color:red' class='fa fa-trash'></span></a>");

				array.put(ja);
			}

			int totalRecords = projectService.getTotalRecordCountdomain();
			int TotalDisplayRecords = projectService.getTotalDisplayRecordsdomain(sSearch);
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", TotalDisplayRecords);
			result.put("aaData", array);
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/projectResultView")
	public void projectResultView(HttpServletRequest request, HttpServletResponse response) {

		JSONObject result = new JSONObject();
		String pageNo = request.getParameter("iDisplayStart");
		String pageSize = request.getParameter("iDisplayLength");
		String sSearch = request.getParameter("sSearch");

		int numRecords = 10;
		int start = 0;

		if (pageNo != null) {
			start = Integer.parseInt(pageNo);
			if (start < 0) {
				start = 0;
			}
		}
		if (pageSize != null) {
			numRecords = Integer.parseInt(pageSize);
			if (numRecords < 10 || numRecords > 50) {
				numRecords = 10;
			}
		}

		try {

			List<Project> paginationProject = projectService.getPaginationProject(start, numRecords, sSearch);

			JSONArray array = new JSONArray();
			for (Iterator<Project> lstIterator = paginationProject.iterator(); lstIterator.hasNext();) {
				Project dataQltDashboard = lstIterator.next();
				JSONArray ja = new JSONArray();
				ja.put(dataQltDashboard.getIdProject());
				ja.put(dataQltDashboard.getProjectName());
				ja.put(dataQltDashboard.getProjectDescription());
				ja.put("<a href='editProject?idApp=" + dataQltDashboard.getIdProject()
						+ "' data-toggle='confirmation'><span class='fa fa-edit'></span></a>");
				ja.put("<a href='deleteProject?id=" + dataQltDashboard.getIdProject()
						+ "'  data-toggle='confirmation'><span style='color:red' class='fa fa-trash'></span></a>");

				array.put(ja);
			}

			int totalRecords = projectService.getTotalRecordCount();
			int TotalDisplayRecords = projectService.getTotalDisplayRecords(sSearch);
			result.put("iTotalRecords", totalRecords);
			result.put("iTotalDisplayRecords", TotalDisplayRecords);
			result.put("aaData", array);
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-store");
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/addNewProject")
	public ModelAndView addNewProject(ModelAndView model, HttpSession session) {
		List<User> lstUser = null;
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
		if (activeDirectoryFlag.equals("Y")) {
			lstUser = groupService.getAllGroupsfromActiveDirectory();

		} else {
			lstUser = groupService.getAllGroups();
		}

		model.addObject("groupList", lstUser);
		model.setViewName("createProject");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "Add New Project");
		return model;
	}
	@RequestMapping(value = "/addNewDomain")
	public ModelAndView addNewDomain(ModelAndView model, HttpSession session) {
		//List<User> lstUser = null;
		List<Project> lstProject = null;
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
	/*	String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);
		if (activeDirectoryFlag.equals("Y")) {
			lstUser = groupService.getAllGroupsfromActiveDirectory();

		} else {
			lstUser = groupService.getAllGroups();
		}*/
		lstProject = projectService.getAllProjects();
		model.addObject("projectList", lstProject);
		model.setViewName("createDomain");
		model.addObject("currentSection", "User Settings");
		model.addObject("currentLink", "Add New Domain");
		return model;
	}
	@RequestMapping(value = "/deleteProject")
	public void deleteProject(HttpServletRequest req, HttpServletResponse response, HttpSession session,
			@RequestParam Long projectId) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);

		boolean deleteProject = projectService.deleteProject(projectId);

		JSONObject json = new JSONObject();
		if (deleteProject == true) {
			try {
				json.append("success", "Project deleted successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else if (deleteProject == false) {

			try {
				json.append("fail", "Associated Project can't Deleted");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				json.append("error", "There is some problem in deleting Project");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@RequestMapping(value = "/viewProject")
	public ModelAndView viewProject(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		ModelAndView modelAndView = new ModelAndView("viewProject");
		modelAndView.addObject("currentSection", "User Settings");
		modelAndView.addObject("currentLink", "viewProject");

		List<Project> lstProject = projectService.getAllProjects();
		modelAndView.addObject("projectList", lstProject);

		return modelAndView;
	}
	@RequestMapping(value = "/viewDomain")
	public ModelAndView viewDomain(HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		ModelAndView modelAndView = new ModelAndView("viewDomain");
		modelAndView.addObject("currentSection", "User Settings");
		modelAndView.addObject("currentLink", "viewDomain");
		List<Domain> lstDomain = projectDao.getAllDomain();
		modelAndView.addObject("projectList", lstDomain);

		return modelAndView;
	}
	@RequestMapping(value = "/deleteProject", method = RequestMethod.GET)
	public ModelAndView deleteProject(HttpServletRequest req, HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}

		long idProject = Long.parseLong(req.getParameter("id"));
		Project selectedProject = projectService.getSelectedProject(idProject);

		ModelAndView model = new ModelAndView();

		model.addObject("selectedProject", selectedProject);
		model.addObject("currentLink", "DCView");
		model.setViewName("deleteProject");
		return model;

	}

	@RequestMapping(value = "/updateProjectIntoDatabase")
	public void updateProjectIntoDatabase(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam String projectName, @RequestParam String projectDescription,
			@RequestParam String selectedOwnerGroups, @RequestParam int id, @RequestParam String oldProject) {

		String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
		System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

		selectedOwnerGroups = selectedOwnerGroups.replace("[", "");
		selectedOwnerGroups = selectedOwnerGroups.replace("]", "");

		boolean isSuccess = true;
		int isDuplicate = 0;

		int duplicateProject;
		if (!oldProject.equals(projectName))

		{
			duplicateProject = projectService.checkDuplicateProject(projectName);
		} else {

			duplicateProject = 0;
		}

		if (duplicateProject != 0) {
			System.out.println("Project alredy Exist!!");
			isDuplicate = 1;
			isSuccess = false;
		} else {

			long projectId = projectService.updateDataIntoProjectTable(projectName, projectDescription, id);

			if (projectId <= 0) {
				isSuccess = false;
			}

			Long projectid = new Long(id);
			if (activeDirectoryFlag.equals("Y")) {

				int success = projectService.delProjectToGroupAssociationActive(projectid);
				      if(success>0) {System.out.println("Old project Association are Removed");}

			} else {
				int success = projectService.delProjectToGroupAssociation(projectid);
				  if(success>0) {System.out.println("Old project Association are Removed");}
			}

			StringTokenizer tokenizer = new StringTokenizer(selectedOwnerGroups, ",");

			String userId = "";
			while (tokenizer.hasMoreTokens()) {
				userId = tokenizer.nextToken();
				userId = userId.replace("\"", "");
				//Long ursId = -1L;
				if (userId != null && !userId.equals("") && !userId.equals("null")) {
					//ursId = Long.parseLong(userId);

					System.out.println("projectid-->" + projectid);
					System.out.println("ursId-->" + userId);

					if (activeDirectoryFlag.equals("Y")) {

						int update = projectService.updateProjectToGroupAssociationActive(projectid, userId, "Y");
						if (update <= 0) {
							isSuccess = false;

						}
					} else {
						

							int update = projectService.updateProjectToGroupAssociation(projectid, userId, "Y");
							if (update <= 0) {
								isSuccess = false;
							}
						}

					
				}

			}

			JSONObject json = new JSONObject();
			if (isSuccess) {
				try {
					json.append("success", "updated successfully");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else {
				if (isDuplicate == 1) {
					try {
						json.append("duplicate", "There was a problem");
						response.getWriter().println(json);
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						json.append("fail", "There was a problem");
						response.getWriter().println(json);
					} catch (JSONException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@RequestMapping(value = "/addNewProjectIntoDatabase")
	public void addNewProjectIntoDatabase(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam String projectName, @RequestParam String projectDescription,
			@RequestParam String selectedOwnerGroups) {

		selectedOwnerGroups = selectedOwnerGroups.replace("[", "");
		selectedOwnerGroups = selectedOwnerGroups.replace("]", "");

		boolean isSuccess = true;
		int isDuplicate = 0;

		int duplicateProject = projectService.checkDuplicateProject(projectName);

		if (duplicateProject != 0) {
			System.out.println("Project alredy Exist!!");
			isDuplicate = 1;
			isSuccess = false;
		} else {

			long projectId = projectService.insertDataIntoProjectTable(projectName, projectDescription);
			if (projectId <= 0) {
				isSuccess = false;
			}

			StringTokenizer tokenizer = new StringTokenizer(selectedOwnerGroups, ",");

			String userId = "";
			while (tokenizer.hasMoreTokens()) {
				userId = tokenizer.nextToken();
				userId = userId.replace("\"", "");
				Long ursId = -1L;
				if (userId != null && !userId.equals("") && !userId.equals("null")) {
				//	ursId = Long.parseLong(userId);
					
					System.out.println("projectId-->" + projectId);
					System.out.println("userId-->" + userId);
					String activeDirectoryFlag = appDbConnectionProperties.getProperty("isActiveDirectoryAuthentication");
					System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

					if (activeDirectoryFlag.equals("Y")) {
						int update = projectService.insertProjectToGroupAssociationActive(projectId, userId, "Y");
						if (update <= 0) {
							isSuccess = false;
						}

					} else {
						int update = projectService.insertProjectToGroupAssociation(projectId, userId, "Y");
						if (update <= 0) {
							isSuccess = false;
						}
					}
				}
			}

			/*
			 * tokenizer = new StringTokenizer(selectedConsumerGroups, ",");
			 * 
			 * groupId = ""; while (tokenizer.hasMoreTokens()) { groupId =
			 * tokenizer.nextToken(); groupId = groupId.replace("\"", ""); Long grpId = -1L;
			 * if (groupId != null && !groupId.equals("") && !groupId.equals("null")) {
			 * grpId = Long.parseLong(groupId); int update =
			 * projectService.insertProjectToGroupAssociation(projectId, grpId, "N"); if
			 * (update <= 0) { isSuccess = false; } }
			 * 
			 * }
			 */
		}

		JSONObject json = new JSONObject();
		if (isSuccess) {
			try {
				json.append("success", "updated successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			if (isDuplicate == 1) {
				try {
					json.append("duplicate", "There was a problem");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					json.append("fail", "There was a problem");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@RequestMapping(value = "/addNewDomainProjectMapping")
	public void addNewDomainIntoDatabase(ModelAndView model, HttpSession session, HttpServletResponse response,
			@RequestParam String domainName, @RequestParam String domainDescription,
			@RequestParam String selectedProjects) {

		selectedProjects = selectedProjects.replace("[", "");
		selectedProjects = selectedProjects.replace("]", "");

		boolean isSuccess = true;
		int isDuplicate = 0;

		int duplicateDomain = projectService.checkDuplicateDomain(domainName);
		if (duplicateDomain != 0) {
			System.out.println("Domain alredy Exist!!");
			isDuplicate = 1;
			isSuccess = false;
		} else {
			long domainId = projectService.insertDataIntoDomainTable(domainName, domainDescription);
			if (domainId <= 0) {
				isSuccess = false;
			}

			StringTokenizer tokenizer = new StringTokenizer(selectedProjects, ",");

			String projId = "";
			while (tokenizer.hasMoreTokens()) {
				projId = tokenizer.nextToken();
				projId = projId.replace("\"", "");
				Long ursId = -1L;
				if (projId != null && !projId.equals("") && !projId.equals("null")) {
					// ursId = Long.parseLong(userId);

					System.out.println("projectId-->" + domainId);
					System.out.println("userId-->" + projId);
					String activeDirectoryFlag = appDbConnectionProperties
							.getProperty("isActiveDirectoryAuthentication");
					System.out.println("activeDirectoryFlag-->" + activeDirectoryFlag);

					int update = projectService.insertDomainToProjectAssociation(domainId, projId, "Y");
					if (update <= 0) {
						isSuccess = false;
					}
				}
			}
		}

		JSONObject json = new JSONObject();
		if (isSuccess) {
			try {
				json.append("success", "updated successfully");
				response.getWriter().println(json);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			if (isDuplicate == 1) {
				try {
					json.append("duplicate", "There was a problem");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					json.append("fail", "There was a problem");
					response.getWriter().println(json);
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}