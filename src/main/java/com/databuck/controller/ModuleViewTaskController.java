/*package com.databuck.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.databuck.bean.Task;
import com.databuck.dao.ITaskDAO;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

@Controller
public class ModuleViewTaskController {

	@Autowired
	public ITaskDAO taskdao;

	@RequestMapping(value = "/taskview")
	public ModelAndView listTask(ModelAndView model, HttpSession session) throws IOException {
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		List<Task> task = taskdao.getData();
		System.out.println(task);
		model.addObject("task", task);
		model.setViewName("taskview");
		return model;
	}

	@RequestMapping(value = "/savetask", method = RequestMethod.GET)
	public ModelAndView getLoginPage(HttpSession session)
	{
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		return new ModelAndView("savetask");
	}

	@RequestMapping(value = "/save", method = RequestMethod.GET)
	public ModelAndView getLogiPage() {
		return new ModelAndView("task_process");
	}


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveTask(@RequestParam("task") String task, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		String msg = " Please Enter Task Name";
		if (task != null && task.trim().length() > 0) {
			int count = taskdao.saveTask(task);
			if (count > 0)
				return new ModelAndView("task_process");
			else
				return new ModelAndView("task_failed");
		}
		return new ModelAndView("savetask", "msg", msg);
	}


	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public  ModelAndView getDeletePage(HttpServletRequest request,HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		System.out.println("request.getParameter Delete task:(/delete)"	+ request.getParameter("idTask"));
		long idTask = (long) Integer.parseInt(request.getParameter("idTask"));
		Task task= new Task();
		task.setIdTask(idTask);
		ModelAndView model = new ModelAndView("deleteTask");
		model.addObject("task",task);	 
		return model;
	}



	*//**
	 * 
	 * Delete operation
	 * 
	 * @param request
	 * @return
	 *//*

	@RequestMapping(value = "/deleteTask", method = RequestMethod.GET)
	public ModelAndView deleteTask(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		long count = 0;
		System.out.println("request.getParameter Delete task:((delecteTask)" + request.getParameter("idTask"));
		long idTask = (long) Integer.parseInt(request.getParameter("idTask"));
		count = taskdao.deleteTask(idTask);
		if (count > 0)
			return new ModelAndView("delete_task");
		else
			return new ModelAndView("delete_task_failed");
	}







	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ModelAndView editTask(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:"+user);
		if((user==null)||(!user.equals("validUser")))
		{
			return new ModelAndView("loginPage");
		}
		int idTask = Integer.parseInt(request.getParameter("idTask"));
		System.out.println("Update Task id "+idTask);
		Task task=taskdao.get(idTask);
		ModelAndView model = new ModelAndView("updateTask");
		model.addObject("task",task);
		return model;
	}



	*//**
	 * 
	 * 
	 * update the task
	 * @param request
	 * @return
	 *//*

	@RequestMapping(value = "/updateTask", method = RequestMethod.GET)
	public ModelAndView updateTask(HttpServletRequest request, HttpSession session) {
		Object user = session.getAttribute("user");
        System.out.println("user:" + user);
        if ((user == null) || (!user.equals("validUser"))) {
            return new ModelAndView("loginPage");
        }
		try
		{
			int idTask = Integer.parseInt(request.getParameter("idTask"));
			String taskName=request.getParameter("taskName");
			System.out.println("Update Task id for updateTask "+idTask);
			System.out.println("Update Task id for updateTask "+taskName);
			Task task= new Task();
			task.setIdTask(idTask);
			task.setTaskName(taskName);
			taskdao.saveOrUpdate(task);
			return new ModelAndView("updateTaskSuccess");
			// model.addObject("task",task);
		}
		catch(Exception e)
		{
			return new ModelAndView("updateTask");
		}
	}
}
*/