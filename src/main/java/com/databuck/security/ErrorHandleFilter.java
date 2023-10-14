/*package com.databuck.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ErrorHandleFilter implements Filter {

	@Override
	public void destroy() {
		// ...
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//
		System.out.println("init");
	}

	
	 * * public void customMethod(HttpServletRequest request){ HttpSession
	 * session=request.getSession(); Map<String,String> module
	 * =(Map<String,String>) session.getAttribute("module");
	 * System.out.println("module="+module); for (Map.Entry m :
	 * module.entrySet()) {
	 * System.out.println("idTask="+m.getKey()+"accessControl="+m.getValue()); }
	 * }
	 

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			
			 * HttpServletRequest req=(HttpServletRequest)request; HttpSession
			 * session=req.getSession(); session=req.getSession();
			 * Map<String,String> module=(Map<String,String>)
			 * session.getAttribute("module");
			 * System.out.println("module="+module); for (Map.Entry m :
			 * module.entrySet()) {
			 * System.out.println("idTask="+m.getKey()+"accessControl="+m.
			 * getValue()); } System.out.println("ErrorHandleFilter");
			 

			chain.doFilter(request, response);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error");
		}

	}

}*/