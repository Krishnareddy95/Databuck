package com.databuck.restcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.databuck.service.ExecutiveSummaryService;

import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

@RestController
@RequestMapping({ "/dbconsole/versionInfo" })
public class VersionInfo {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate1;

	@Autowired
	ExecutiveSummaryService executiveSummaryService;

	private static final Logger LOG = Logger.getLogger(VersionInfo.class);

	@GetMapping
	public ResponseEntity<Object> getVersionInfo(@RequestHeader HttpHeaders headers) throws IOException {

		JSONObject json = new JSONObject();
		String status = "failed";
		String message = "";
		String token = "";
		Map<String, String> envProps = new LinkedHashMap<>();

		Map<String, Map<String, String>> result = new HashMap<>();
		// Default response status
		HttpStatus responseStatus = HttpStatus.OK;

		try {
			// Get token from request header
			try {
				token = headers.get("token").get(0);
				LOG.debug("token " + token.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Validate token
			if (token != null && !token.isEmpty()) {

				// Check if token is expired or not
				JSONObject tokenStatusObj = executiveSummaryService.validateToken(token);
				String tokenStatus = tokenStatusObj.getString("status");

				if (tokenStatus.equals("success")) {

					final ClassPathResource gitPropResource = new ClassPathResource("git.properties");
					if (!gitPropResource.exists()) {
						throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY,
								"git.properties not found. Are you running from jar?");
					}
					Properties gitProps = new Properties();
					try (final InputStream is = gitPropResource.getInputStream()) {
						gitProps.load(is);
					} catch (IOException e) {
						System.out.println("Failed to read " + gitPropResource.getPath() + e);
					}
					result.put("App Server Git", (Map) gitProps);

					Map<String, Object> rs = jdbcTemplate.queryForMap(
							"select version, script from schema_version where installed_rank in (select max(installed_rank) from schema_version)");
					for (Map.Entry<String, Object> entry : rs.entrySet()) {
						System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						envProps.put("app.db.schema.version", rs.get("version").toString());
						envProps.put("app.db.schema.log", rs.get("script").toString());
					}

					Map<String, Object> rs1 = jdbcTemplate1.queryForMap(
							"select version, script from schema_version where installed_rank in (select max(installed_rank) from schema_version)");
					for (Map.Entry<String, Object> entry : rs1.entrySet()) {
						System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
						envProps.put("results.db.schema.version", rs1.get("version").toString());
						envProps.put("results.db.schema.log", rs1.get("script").toString());
					}

					String databuckHome = "/opt/databuck";

					if (System.getenv("DATABUCK_HOME") != null && !System.getenv("DATABUCK_HOME").trim().isEmpty()) {

						databuckHome = System.getenv("DATABUCK_HOME");

					} else if (System.getProperty("DATABUCK_HOME") != null
							&& !System.getProperty("DATABUCK_HOME").trim().isEmpty()) {

						databuckHome = System.getProperty("DATABUCK_HOME");

					}

					File dir = new File(databuckHome);
					File[] files = dir.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".jar");
						}
					});

					for (File jarFile : files) {
						final JarFile jarFile1 = new JarFile(jarFile.toString());
						result.put(jarFile.getName(), readContent(jarFile1));
					}
					status = "success";
					message = "success";

				} else {
					message = "Token expired.";
					LOG.error("Token is expired.");
					responseStatus = HttpStatus.EXPECTATION_FAILED;
				}

			} else {
				message = "Token is missing in headers";
				LOG.error(message);
			}
		} catch (Exception e) {
			message = "Request failed";
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
		json.put("status", status);
		json.put("message", message);
		result.put("database", envProps);
		json.put("result", result);
		return new ResponseEntity<Object>(json.toString(), responseStatus);
	}

	private Map<String, String> readContent(JarFile jarFile) {
		Map<String, String> fileOutput = new HashMap<String, String>();
		try {
			if (jarFile != null) {
				final Enumeration<JarEntry> entries = jarFile.entries(); // get entries from the zip file...
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					if (entry.getName().equalsIgnoreCase("git.properties")) {
						System.out.println("File : " + entry.getName());
						JarEntry fileEntry = jarFile.getJarEntry(entry.getName());
						InputStream input = jarFile.getInputStream(fileEntry);
						InputStreamReader isr = new InputStreamReader(input);
						BufferedReader reader = new BufferedReader(isr);
						String line;
						int i = 0;
						while ((line = reader.readLine()) != null) {
							fileOutput.put(String.valueOf(i++), line.toString());
							System.out.println(line);
						}
						reader.close();

					}
				}
			}

		} catch (Exception e) {
			System.out.println("Exception occurred " + e);
			return null;
		}
		return fileOutput;
	}

}
