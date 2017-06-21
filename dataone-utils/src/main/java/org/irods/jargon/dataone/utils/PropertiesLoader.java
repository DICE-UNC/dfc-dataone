package org.irods.jargon.dataone.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {

	private Properties properties;

	private String propertiesFilename = "/etc/irods-ext/d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public PropertiesLoader() {

		properties = new Properties();
		InputStream input = null;
		File propsFile = new File(propertiesFilename);
		if (!propsFile.exists()) {
			log.error("illegal state exception, cannot find /etc/irods-ext/dlclient.properties");
			throw new IllegalStateException("unable to load properties file at /etc/irods-ext/dlclient.properties");
		}

		try {
			input = new FileInputStream(propsFile);
		} catch (FileNotFoundException e1) {
			log.error("cannot open /etc/irods-ext/dlclient.properties");
			throw new IllegalStateException("unable to stream properties file at /etc/irods-ext/dlclient.properties");
		}

		// input = getClass().getClassLoader().getResourceAsStream(
		// propertiesFilename);

		// load a properties file
		try {
			properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", propertiesFilename);
			log.error("IOException: {}", e);
			properties = new Properties();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/**
	 * Get all available properties
	 * 
	 * @return {@link Properties} configured in etc
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Get an individual property from etc by key
	 * 
	 * @param key
	 *            <code>String</code> with the prop key
	 * @return <code>String</code> with the property value, or <code>null</code>
	 */
	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

}
