/*package com.databuck.security;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
@Configuration
public class ExpiredPasswordFilter extends OncePerRequestFilter {

	
 protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
  System.out.println("It is in the filter");
  
   *  This is where I did my user flag checking
   
  
                chain.doFilter(req, res);
 }
}
*/