package com.databuck.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan("com.databuck")
@EnableSwagger2
public class SwaggerConfig { 
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
	
	 @Bean
	    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.tags(new Tag("Template", "Template API"), new Tag("Connection", "Connection API"),
        				new Tag("Validation", "Validation API"),new Tag("Project", "Project API"),
        				new Tag("Login", "Login API"),new Tag("Schema", "Schema API"),
        				new Tag("Catalog", "Catalog API"),new Tag("DataQuality", "Data Quality API"),
        				new Tag("Domain", "Domain API"),new Tag("Notification", "Notification API"),
        				new Tag("AppGroup", "AppGroup API"))
        		.apiInfo(apiInfo())
        		.select().apis(RequestHandlerSelectors.any())
                .paths(paths(getAPI(),new ArrayList<String>())).build()
                //.globalOperationParameters(globalParameterList())
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()));
	    }

	 private List<String> getAPI(){
		ArrayList<String> path = new ArrayList<String>();
	 	path.add("/restapi/project/getAllProjects");  
		path.add("/restapi/dataconnection/getDataConnectionsForProject");
		path.add("/restapi/datatemplate/getDataTemplatesForSchema");
		path.add("/restapi/datatemplate/getDataTemplateById");
		path.add("/restapi/datatemplate/getAdvancedRules");
		path.add("/restapi/datatemplate/getMetadata");
		path.add("/restapi/datatemplate/getTemplatesColumnChangesForProject");
		path.add("/restapi/validation/getAllValidationChecks");
		path.add("/restapi/validation/getValidationCheckById");
		path.add("/restapi/validation/runValidation");
		path.add("/restapi/project/runProject");
		path.add("/restapi/project/checkProjectJobStatusById");
		path.add("/restapi/project/getProjectJobHistory");
		path.add("/restapi/validation/checkStatus");
		path.add("/restapi/dataquality/dashboardResultById");
		path.add("/restapi/dataquality/dashboardResultDetailsById");
		path.add("/restapi/dataquality/dashboardResult");
		path.add("/restapi/createTemplateValidationJob");
		path.add("/restapi/catalog/appList");
		path.add("/restapi/catalog/appResult");
		path.add("/restapi/catalog/updateRuleCatalogApprovalStatus");
		path.add("/restapi/dataconnection");
		path.add("/restapi/dataconnection/deactivateConnection");
		path.add("/restapi/dataconnection/activateConnection");
		path.add("/restapi/project/runProjectByName");
		path.add("/restapi/schema/killSchemaJob");
		path.add("/restapi/validation/killValidationRun");
		path.add("/restapi/template/checkTemplateJobStatusById");
		path.add("/restapi/template/reRunTemplate");
		path.add("/restapi/domain/runDomain");
		path.add("/restapi/project/getDomainToProjectMapping");
		path.add("/restapi/notification/getFailedAPINotifications");
		path.add("/restapi/catalog/getAppResultForRun");//
		path.add("/restapi/schema/runSchema");
		path.add("/restapi/appgroup/runAppGroupByName");
		path.add("/restapi/aging/getAgingIssuesForValidation");
		path.add("/restapi/createValidationCheck");
		path.add("/restapi/createDataTemplate");
		path.add("/restapi/createDataConnection");
		path.add("/restapi/login");
		return path;
	 }

	    private ApiInfo apiInfo() {
	        return new ApiInfoBuilder().title("Databuck API Documentation").description("Databuck documentation of open API's for clients")
	        		.contact(new Contact("Contact", "https://firsteigen.com/contact-us/", "contact@firsteigen.com")).version("1.0.0")
	                .termsOfServiceUrl("https://firsteigen.com/privacy-policy/").license("License")
	                .licenseUrl("https://firsteigen.com/privacy-policy/").build();
	    }
	    
	    private ApiKey apiKey() {
	        return new ApiKey("Authorization", "Authorization", "header");
	    }
	    
	    private SecurityContext securityContext() {
	        return SecurityContext.builder()
	            .securityReferences(defaultAuth())
	            .forPaths(paths(getAPI(),new ArrayList<String>()))
	            .build();
	    }

	    List<SecurityReference> defaultAuth() {
		    AuthorizationScope authorizationScope
		        = new AuthorizationScope("global", "accessEverything");
		    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		    authorizationScopes[0] = authorizationScope;
		    return new ArrayList<SecurityReference>(){{
		        new SecurityReference("Authorization", authorizationScopes);}};
		  }
	    
	    @Bean
		SecurityConfiguration security() {
			return new SecurityConfiguration(null, null, null, null, "token", ApiKeyVehicle.HEADER,"token", ",");
		}
	    
	
	    private com.google.common.base.Predicate<String> paths(List<String> basePath,List<String> excludePath) { 
	    	if (basePath.isEmpty()) {
	    	 basePath.add("/**"); 
	    	}
	    	List<com.google.common.base.Predicate<String>> basePathList = new ArrayList<>(); 
	    	for (String path : basePath) {
	    		basePathList.add(PathSelectors.ant(path)); 
	    	}
	    	List<com.google.common.base.Predicate<String>> excludePathList = new ArrayList<>();
	    	for (String path : excludePath) {
	    		excludePathList.add(PathSelectors.ant(path)); 
	    	}
	    	return Predicates.and(Predicates.not(Predicates.or(excludePathList)),Predicates.or(basePathList)); 
	    }
	 }