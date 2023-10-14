package com.databuck.exception;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Component
public class ExceptionHandler implements HandlerExceptionResolver
{
	static Logger logger= Logger.getLogger(ExceptionHandler.class.getName());
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,Exception exception) {
		
		exception.printStackTrace();
		ModelAndView model= new ModelAndView();
		model.addObject("exception",exception);
		model.addObject("url",request.getRequestURI());
		model.setViewName("errorPage");
		return model;
	}
	@RequestMapping(value = "/notFound")
	public ModelAndView send404Page()
	{
		return new ModelAndView("notFound");
	}
}