package com.databuck.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.databuck.constants.DatabuckConstants;
import com.databuck.dao.ITaskDAO;
import org.apache.commons.dbcp.BasicDataSource;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.databuck.bean.ListDataSchema;
import com.databuck.dao.IRoleDAO;
import com.databuck.dao.SchemaDAOI;
import org.apache.log4j.Logger;

@Service
public class ConnectionUtil 
{
	
	@Autowired
	private SchemaDAOI schemadao;

	@Autowired
	private Properties appDbConnectionProperties;

	@Autowired
	private ITaskDAO taskDAO;
	
	private static final Logger LOG = Logger.getLogger(ConnectionUtil.class);

	
	public JdbcTemplate getJDBCTemplate(Long idDataSchema)
	{
	/*	idDataSchemaForDS = idDataSchema;
		
		LOG.debug("setIdDataSchemaForDS =>"+idDataSchema);*/
	
	
	/*@Bean
	public DataSource dataSourceForImport() throws SQLException {*/
		//Properties prop = appDbConnectionProperties();
		BasicDataSource dataSource;
			
		LOG.debug("DataSource =>idDataSchemaForDS =>"+idDataSchema);
		
		List<ListDataSchema> listdataDetails = schemadao.readdatafromlistdataschema(idDataSchema); // here putting idDataSchema for testing
		
		ListDataSchema ListDataSchemaobj=(ListDataSchema)listdataDetails.get(0);
		//LOG.debug("new :"+ListDataSchemaobj.getIpAddress());
		String uri=ListDataSchemaobj.getIpAddress();
		String database=ListDataSchemaobj.getDatabaseSchema();
		String username=ListDataSchemaobj.getUsername();
		String password=ListDataSchemaobj.getPassword();
		String port=ListDataSchemaobj.getPort();
		String sslEnb = ListDataSchemaobj.getSslEnb();
	
		
		//String url = "jdbc:mysql://" + uri + ":" + port;
		//jdbc\:mysql\://96.74.138.89\:3306/databuck_app_db



		dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + uri + ":" + port +"/"+database;

		if(sslEnb!=null && sslEnb.trim().equalsIgnoreCase("Y"))
			url= url+"?verifyServerCertificate=false&useSSL=true";
	
		LOG.debug("url==============>"+url);
		dataSource.setUrl(url);
		
		dataSource.setUsername(ListDataSchemaobj.getUsername());
		
		StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
		decryptor.setPassword("7rmaHWOxLPfjSPz4bHA6");
		  String decryptedText = decryptor.decrypt(ListDataSchemaobj.getPassword());
		
		dataSource.setPassword(decryptedText);
		/*dataSource.setPassword("");*/
		LOG.debug("DataSource =>"+dataSource.getUrl());
		
		//return dataSource;
		return new JdbcTemplate(dataSource);
	}
	/*@Bean
	public JdbcTemplate jdbcTemplateForImport() throws SQLException {
		return new JdbcTemplate(dataSourceForImport());
	}
		*/

	public String getRemoteClusterUrlByIdDataSchema(long idDataSchema,String apiCallName){
//		String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";

		String URI= DatabuckConstants.URI_PREFIX;
		String context= DatabuckConstants.API_CONTEXT;

		try {
			String hostContext = taskDAO.getClusterCategoryNameBySchemaId(idDataSchema).get("cluster_property_category");;

			if(hostContext==null || hostContext.isEmpty() ||hostContext.trim().equalsIgnoreCase(DatabuckConstants.DEAFAULT_CLUSTER_CATEGORY)
				|| hostContext.trim().equalsIgnoreCase("local")){
				URI="";
			}else{
//				String hostContext= clusterCategory.split("_")[1];
				String host_uri = appDbConnectionProperties.getProperty("proxy_"+hostContext);
				
				if(host_uri.contains("http"))
					URI = host_uri.trim()+context+apiCallName;
				else
					URI = URI+host_uri.trim()+context+apiCallName;
			}

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
			URI="";
		}

		return URI;

	}

	public String getRemoteClusterUrlByClusterCategory(String hostContext,String apiCallName){
//		String publishUrl="https://140.238.249.1:8085/cdp/testDataConnection";

		String URI= DatabuckConstants.URI_PREFIX;
		String context= DatabuckConstants.API_CONTEXT;

		try {
			if(hostContext==null || hostContext.isEmpty() ||hostContext.trim().equalsIgnoreCase(DatabuckConstants.DEAFAULT_CLUSTER_CATEGORY)){
				URI="";
			}else{
//				String hostContext= clusterCategory.split("_")[1];
				String host_uri = appDbConnectionProperties.getProperty("proxy_"+hostContext);
				URI = URI+host_uri.trim()+context+apiCallName;
			}

		}catch (Exception e){
			LOG.error(e.getMessage());
			e.printStackTrace();
			URI="";
		}

		return URI;

	}

}
