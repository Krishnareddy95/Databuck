/*package com.databuck.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

*//**
 * Spring Web MVC Security Java Config Demo Project Configures authentication
 * and authorization for the application.
 *
 * @author www.codejava.net
 *
 *//*
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired(required = true)
	private HttpServletRequest request;
	
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception { auth .inMemoryAuthentication()
	 * .withUser("admin").password("nimda").roles("ADMIN"); }
	 

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		System.out.println("SecurityConfig");
		
		 * Map<String,String> module =(Map<String,String>)
		 * session.getAttribute("module"); System.out.println("module="+module);
		 * for (Map.Entry m : module.entrySet()) {
		 * System.out.println("idTask="+m.getKey()+"accessControl="+m.getValue()
		 * ); }
		 
		try {
			HttpSession session = request.getSession();
			if (session != null) {
				Map<String, String> module = (Map<String, String>) session.getAttribute("module");
				System.out.println("module=" + module);
				for (Map.Entry m : module.entrySet()) {
					System.out.println("idTask=" + m.getKey() + "accessControl=" + m.getValue());
				}
			}

			http.authorizeRequests().antMatchers("/").permitAll().antMatchers("/admin**").access("hasRole('ADMIN')")
					.and().formLogin();

			http.csrf().disable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/