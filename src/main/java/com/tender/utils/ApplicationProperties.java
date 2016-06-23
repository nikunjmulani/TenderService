package com.tender.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationProperties {
	private static final Logger LOGGER = Logger.getLogger(ApplicationProperties.class);
	private final static String PROP_FILE_NAME = "tenderconfig.properties";
	private static Properties prop = null;
	
	static {
		try {
			init();
		} catch (IOException e) {
			LOGGER.error("Exception while load config file:",e);
		}
	}

	public static void init() throws IOException {
		prop = new Properties();
		InputStream inputStream = null;
		String propFileName = "";

		// LOAD CONFIG FILE FROM TOMCAT CONFIG FOLDER
		try {
			final String inputStream1 = System.getenv("CATALINA_HOME");

			if (inputStream1 != null && !"".equals(inputStream1)) {
				propFileName = inputStream1 + "\\conf\\" + PROP_FILE_NAME;
				inputStream = new FileInputStream(propFileName);
				if (inputStream != null) {
					prop.load(inputStream);
				}

			}
		} catch (Exception e) {
			LOGGER.error("APPLICATION CONFIG FILE IS NOT FOUND:" + propFileName, e);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		// LOAD DEFAULT CONFIG FILE
		if (prop.isEmpty()) {
			try {
				inputStream = ApplicationProperties.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME);
				if (inputStream != null) {
					prop.load(inputStream);
				}
			} catch (Exception e) {
				LOGGER.error("DEFAULT APPLICATION CONFIG FILE IS NOT FOUND:" + PROP_FILE_NAME, e);
			} finally {
				if (inputStream != null)
					inputStream.close();
			}
		}
		LOGGER.info("APPLICATION PROPERTIES:"+prop);
	}
	
	public static String getProperty(String key) {
		return prop == null ? null : prop.getProperty(key);
	}
}