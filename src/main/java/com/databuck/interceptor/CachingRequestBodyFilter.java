package com.databuck.interceptor;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class CachingRequestBodyFilter extends OncePerRequestFilter{
	
	@Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
		if(!parameterNames.hasMoreElements()) {
	        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(httpServletRequest);
	        filterChain.doFilter(cachedBodyHttpServletRequest, httpServletResponse);
        }else {
        	filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
