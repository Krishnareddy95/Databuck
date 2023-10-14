package com.databuck.datatemplate;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.metadata.schema.ColumnMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;

@Component
public class CassandraConnection {
	public Object[] readTablesFromCassandra(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port) {
		Map<String, String> tableData = new LinkedHashMap<String, String>();
		List<String> primaryKeyColumns = new ArrayList<String>();
		try {
			CqlSessionBuilder builder = CqlSession.builder();
	        builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port))).withAuthCredentials(username, password);

	        CqlSession cassandraSession = builder.build();
	        
			ResultSet rs = cassandraSession.execute("SELECT * FROM " + databaseAndSchema + "." + tablename);
			Iterator<ColumnDefinition> asList = rs.getColumnDefinitions().iterator();
			
			int columnCount = 0;
			while(asList.hasNext()) {
				columnCount++;
				ColumnDefinition def = asList.next();
				System.out.println(def.getName().asInternal() + "   " + def.getType());
				tableData.put(def.getName().asInternal(), String.valueOf(def.getType()));
			}

			System.out.println(columnCount);
			Optional<KeyspaceMetadata> keyspaceMetadata = cassandraSession.getMetadata().getKeyspace(databaseAndSchema);
			if(keyspaceMetadata.isPresent()) {
				Optional<TableMetadata> tableMetadata = keyspaceMetadata.get().getTable(tablename);

				if(tableMetadata.isPresent()) {
					List<ColumnMetadata> columns = tableMetadata.get().getPrimaryKey();
					for (ColumnMetadata columnMetadata : columns) {
						System.out.println(columnMetadata.getName().asInternal());
						primaryKeyColumns.add(columnMetadata.getName().asInternal());
					}
				}
			}
			cassandraSession.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[] { tableData, primaryKeyColumns };
	}
	public List<String> readPrimaryKeysFromCassandra(String uri, String databaseAndSchema, String username, String password,
			String tablename, String port) {
		List<String> primaryKeyColumns = new ArrayList<String>();
		try {
			
			CqlSessionBuilder builder = CqlSession.builder();
	        builder.addContactPoint(new InetSocketAddress(uri, Integer.valueOf(port))).withAuthCredentials(username, password);

	        CqlSession cassandraSession = builder.build();
			Optional<KeyspaceMetadata> keyspaceMetadata = cassandraSession.getMetadata().getKeyspace(databaseAndSchema);
			if(keyspaceMetadata.isPresent()) {
				Optional<TableMetadata> tableMetadata = keyspaceMetadata.get().getTable(tablename);
	
				if(tableMetadata.isPresent()) {
				List<ColumnMetadata> columns = tableMetadata.get().getPrimaryKey();
					for (ColumnMetadata columnMetadata : columns) {
						primaryKeyColumns.add(columnMetadata.getName().asInternal());
					}
				}
			}

			cassandraSession.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryKeyColumns;
	}
	
	public List<String> getListOfTableNamesFromCassandra(String hostURI, String userlogin, String password, String port,
			String database, String domain) {
		List<String> tableNames = new ArrayList<String>();
		try {
			CqlSessionBuilder builder = CqlSession.builder();
	        builder.addContactPoint(new InetSocketAddress(hostURI, Integer.valueOf(port))).withAuthCredentials(userlogin, password);

	        CqlSession cassandraSession = builder.build();
	        
			Map<CqlIdentifier,TableMetadata> tables = cassandraSession.getMetadata()
			    .getKeyspace(database).get()
			    .getTables(); // TableMetadata has name in getName(), along with lots of other info

			for(CqlIdentifier cql:tables.keySet()) {
				tableNames.add(tables.get(cql).getName().asInternal());
			}
			cassandraSession.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNames;
	}

}