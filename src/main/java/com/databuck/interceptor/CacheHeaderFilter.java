package com.databuck.interceptor;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class CacheHeaderFilter extends GenericFilterBean {

	private LinkedHashMap<String, CacheMode> cacheMap = new LinkedHashMap<>(); // sorted for pattern priority
	private AntPathMatcher matcher = new AntPathMatcher();

	public CacheHeaderFilter(LinkedHashMap<String, CacheMode> cacheMap) {
		this.cacheMap = cacheMap;
	}

	public static void configCacheHeaders(CacheMode mode, HttpServletResponse res) {
		switch (mode) {
		case FORCE_CHECK:
			res.setHeader("Cache-Control", "private, max-age=0");
			break;
		case NO_CACHE:
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			res.setHeader("Pragma", "no-cache");
			res.setDateHeader("Expires", 0);
			break;
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		String servletPath = ((HttpServletRequest) servletRequest).getServletPath();
		HttpServletResponse res = (HttpServletResponse) servletResponse;

		for (String path : cacheMap.keySet()) {
			if (matcher.match(path, servletPath)) {
				configCacheHeaders(cacheMap.get(path), res);
				break;
			}
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public enum CacheMode {
		NO_CACHE, FORCE_CHECK
	}
}
