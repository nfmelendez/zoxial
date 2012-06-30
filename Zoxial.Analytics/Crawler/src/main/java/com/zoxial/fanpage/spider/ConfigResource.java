package com.zoxial.fanpage.spider;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Represents the config resource, it loads the configuration one time when the
 * classloader load this class and then properties must be read and never write
 * into this resource. Actually the files path is in config/config.properties
 * and each property of the config file is documented inside it. This resource
 * is to load once, and read forever. That make it threadsafe. Change the
 * properties after it was load is forgiven, but not enforced. If you change a
 * property after it loaded, then there could exist a race condition. It is an
 * eager Singleton.
 * 
 * @author @nfmelendez nfmelendez@gmail.com
 * 
 */
public class ConfigResource {

	/** Logger */
	private static final Logger log = Logger.getLogger(ConfigResource.class);

	/** The only instance of ConfigResouce in all the jvm, Cannot be null */
	public static Configuration INSTANCE;

	/** Private constructor to disable creation from outside */
	private ConfigResource() {
	}

	/**
	 * Method executed by the clasloader since it is an eager Singleton.
	 */
	static {
		String filename = "config/config.properties";

		try {
			INSTANCE = new PropertiesConfiguration(filename);
			log.info("Configuration was succefully created.");
		} catch (ConfigurationException e1) {
			log.error("Something wrong happen with configurations", e1);
		}
	}

}
