package com.zoxial.application;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class ConfigResource {

	private static final Logger log = Logger.getLogger(ConfigResource.class);
	public static Configuration INSTANCE;

	private ConfigResource() {
	}

	static {
		String filename = "config/config.properties";

		try {
			INSTANCE = new PropertiesConfiguration(filename);
			log.info("Configuration was succefully created.");
		} catch (ConfigurationException e) {
			log.error(
					"Something wrong happen with configurations, it should be in path: "
							+ filename, e);
		}
	}

}