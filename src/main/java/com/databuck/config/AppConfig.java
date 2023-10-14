package com.databuck.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;

@Service
@Configuration
@Profile("local")
@EnableWebMvc
@EnableAsync
@EnableTransactionManagement
@ComponentScan("com.databuck")
public class AppConfig implements Serializable, WebMvcConfigurer {

	private static final long serialVersionUID = 1L;

	@Autowired
	private DatabuckPropertyInitializer databuckPropertyInitializer;

	@Bean
	public Properties appDBInit() {
		java.util.Properties propFile = new java.util.Properties();
		try {
			InputStream is = new FileInputStream(
					new File(System.getenv("DATABUCK_HOME") + "/propertiesFiles/appdb.properties"));
			propFile.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		propFile = databuckPropertyInitializer.readAppDBProperties(propFile);

		return propFile;
	}

	@Bean
	public Properties resultDBInit() {
		java.util.Properties propFile = new java.util.Properties();
		try {
			InputStream is = new FileInputStream(
					new File(System.getenv("DATABUCK_HOME") + "/propertiesFiles/resultsdb.properties"));
			propFile.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		propFile = databuckPropertyInitializer.readResultDBProperties(propFile);
		return propFile;
	}

	// appdb DataSource
	@Bean
	public DataSource dataSource() throws SQLException {
		Properties prop = appDBInit();
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(prop.getProperty("db.driver"));
		String url = prop.getProperty("db.url");
		dataSource.setUrl(url);
		dataSource.setUsername(prop.getProperty("db.user"));
		dataSource.setPassword(prop.getProperty("db.pwd"));
		return dataSource;
	}

	// results db DataSource
	@Bean
	public DataSource dataSource1() {
		Properties prop = resultDBInit();
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(prop.getProperty("db1.driver"));
		String url = prop.getProperty("db1.url");
		dataSource.setUrl(url);
		dataSource.setUsername(prop.getProperty("db1.user"));
		dataSource.setPassword(prop.getProperty("db1.pwd"));
		return dataSource;
	}

	@Bean
	public DataSource dataSource2() {
		Properties prop = resultDBInit();
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(prop.getProperty("db1.driver"));
		dataSource.setUrl(prop.getProperty("db1.ur2"));
		dataSource.setUsername(prop.getProperty("db1.user"));
		dataSource.setPassword(prop.getProperty("db1.pwd"));
		return dataSource;
	}

	// appdb jdbcTemplate
	@Bean
	public JdbcTemplate jdbcTemplate() throws SQLException {
		return new JdbcTemplate(dataSource());
	}

	// results db jdbcTemplate
	@Bean
	public JdbcTemplate jdbcTemplate1() {
		return new JdbcTemplate(dataSource1());
	}

	// ref db jdbcTemplate
	@Bean
	public JdbcTemplate jdbcTemplate2() {
		return new JdbcTemplate(dataSource2());
	}

	// Defining Hibernate SessionFactory bean
	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
		sessionBuilder.addAnnotatedClass(FileMonitorRules.class);
		sessionBuilder.addAnnotatedClass(FileTrackingHistory.class);
		sessionBuilder.addAnnotatedClass(FileTrackingSummary.class);

		sessionBuilder.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		// change this property to false
		sessionBuilder.setProperty("hibernate.show_sql", "false");
		sessionBuilder.setProperty("hibernate.hbm2ddl.auto", "update");
		sessionBuilder.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		sessionBuilder.addSqlFunction("group_concat", new StandardSQLFunction("group_concat", new StringType()));
		return sessionBuilder.buildSessionFactory();
	}

	// Defining Hibernate TrasactionManager
	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		return transactionManager;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	
    @Bean
    public JavaMailSender getJavaMailSender() {
    	Properties prop = appDBInit();
		String host = prop.getProperty("smtp_host");
		String port = prop.getProperty("smtp_port"); 
    	String username= prop.getProperty("smtp_username");	
    	String password= prop.getProperty("smtp_password");
    	
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        try {
        	if(host!=null && port !=null && username !=null && password !=null) {
		        mailSender.setHost(host);
		        mailSender.setPort(Integer.parseInt(port));
		        mailSender.setUsername(username);
		        mailSender.setPassword(password);
		        Properties props = mailSender.getJavaMailProperties();
		        props.put("mail.transport.protocol", "smtp");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.debug", "true");
        	}
        }catch(Exception ce) {
        	ce.printStackTrace();
        }
        return mailSender;
    }
}
