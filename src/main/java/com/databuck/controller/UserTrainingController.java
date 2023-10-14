package com.databuck.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserTrainingController {

	
	@RequestMapping(value = "/UserTrainingVideos",method = RequestMethod.POST)
	public ModelAndView UserTraining(ModelAndView model, HttpSession session) {
		Object user = session.getAttribute("user");
		System.out.println("user:" + user);
		if ((user == null) || (!user.equals("validUser"))) {
			return new ModelAndView("loginPage");
		}
		
			System.out.println("UserTraining/Videos");
			model.setViewName("userTrainingVideos");
			model.addObject("currentSection", "User Training");
			model.addObject("currentLink", "Videos");
			return model;
		
	}
}
