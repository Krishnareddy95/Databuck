package com.databuck.config;

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
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.databuck.bean.FileMonitorRules;
import com.databuck.bean.FileTrackingHistory;
import com.databuck.bean.FileTrackingSummary;

@Service
@Configuration
@Profile("cluster")
@EnableWebMvc
@EnableTransactionManagement
@EnableAsync
@ComponentScan("com.databuck")
public class S3AppConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private DatabuckPropertyInitializer databuckPropertyInitializer;

	@Bean
	public Properties appDBInit() {
		initiallizeS3Constants();
		java.util.Properties s3Prop = new java.util.Properties();
		try {
			AmazonS3 s3Client = getS3Client();
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(S3Constants.S3BUCKET, "appdb.properties"));
			s3Prop.load(s3Object.getObjectContent());
		} catch (Exception e) {
			System.out.println("Problem reading appdb properties file from s3:" + e.getMessage());
			e.printStackTrace();
		}

		s3Prop = databuckPropertyInitializer.readAppDBProperties(s3Prop);
		return s3Prop;
	}

	@Bean
	public Properties resultDBInit() {

		java.util.Properties s3Prop = new java.util.Properties();
		try {
			AmazonS3 s3Client = getS3Client();
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(S3Constants.S3BUCKET, "resultsdb.properties"));
			s3Prop.load(s3Object.getObjectContent());
		} catch (Exception e) {
			System.out.println("Problem reading appdb properties file from s3:" + e.getMessage());
			e.printStackTrace();
		}

		s3Prop = databuckPropertyInitializer.readResultDBProperties(s3Prop);
		return s3Prop;
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

	private AmazonS3 getS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(S3Constants.S3ACCESSKEY, S3Constants.S3SECRETKEY);

		AmazonS3 s3Client = new AmazonS3Client(credentials);
		System.setProperty(SDKGlobalConfiguration.ENFORCE_S3_SIGV4_SYSTEM_PROPERTY, "true");
		System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");
		if (S3Constants.S3REGION.equals("us_east_1")) {
			s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_EAST_1));
		} else if (S3Constants.S3REGION.equals("us_west_1")) {
			s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_1));
		} else if (S3Constants.S3REGION.equals("us_west_2")) {
			s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.US_WEST_2));
		} else if (S3Constants.S3REGION.equals("eu_west_1")) {
			s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1));
		}

		return s3Client;
	}

	private void initiallizeS3Constants() {
		String accessKey = "";
		if (System.getenv("accessKey") != null) {
			accessKey = System.getenv("accessKey");
		} else if (System.getProperty("accessKey") != null) {
			accessKey = System.getProperty("accessKey");
		}

		String secretKey = "";
		if (System.getenv("secretKey") != null) {
			secretKey = System.getenv("secretKey");
		} else if (System.getProperty("secretKey") != null) {
			secretKey = System.getProperty("secretKey");
		}

		String bucketName = "";
		if (System.getenv("bucketName") != null) {
			bucketName = System.getenv("bucketName");
		} else if (System.getProperty("bucketName") != null) {
			bucketName = System.getProperty("bucketName");
		}

		String regionName = System.getenv("regionName");
		if (System.getenv("regionName") != null) {
			regionName = System.getenv("regionName");
		} else if (System.getProperty("regionName") != null) {
			regionName = System.getProperty("regionName");
		}

		System.out.println("accessKey:" + accessKey);
		System.out.println("secretKey:" + secretKey);
		System.out.println("regionName:" + regionName);
		System.out.println("bucket name:" + bucketName);

		S3Constants.initiallizeS3Constants(accessKey, secretKey, regionName, bucketName);
	}

}
