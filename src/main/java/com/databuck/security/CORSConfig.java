package com.databuck.security;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SuppressWarnings("deprecation")
@Component
public class CORSConfig {

	@Autowired
	private Properties appDbConnectionProperties;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				String[] hostNames = {};
				if (appDbConnectionProperties.containsKey("hostNamesForCORS")) {
					hostNames = appDbConnectionProperties.getProperty("hostNamesForCORS").split(";");
				}
				registry.addMapping("/dbconsole/*").allowedOrigins(hostNames);
			}
		};
	}
}
