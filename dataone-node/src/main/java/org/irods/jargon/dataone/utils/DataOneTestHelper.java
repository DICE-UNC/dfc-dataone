package org.irods.jargon.dataone.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irods.jargon.testutils.TestingUtilsException;

/**
 * Utilities to load testing properties from a properties file
 *
 * @author Mike Conway, DICE (www.irods.org)
 * @since 10/18/2009
 */
public class DataOneTestHelper {

	public static final String JAR_DIRECTORY_PROP = "plugin.jar.location";

	public String getPluginJarLocation(final Properties testingProperties) {
		return testingProperties.getProperty(JAR_DIRECTORY_PROP);
	}

	/**
	 * Return the given property (by key) as an int
	 *
	 * @param testingProperties
	 * @param key
	 * @return
	 * @throws TestingUtilsException
	 */
	public int getPropertyValueAsInt(final Properties testingProperties,
			final String key) throws TestingUtilsException {
		String propVal = (String) testingProperties.get(key);

		if (propVal == null || propVal.length() == 0) {
			throw new TestingUtilsException(
					"missing or invalid value in test-dataone.properties");
		}

		int retVal = 0;

		try {
			retVal = Integer.parseInt(propVal);
		} catch (NumberFormatException nfe) {
			throw new TestingUtilsException(
					"port is in valid format to convert to int:" + propVal, nfe);
		}

		return retVal;
	}

	/**
	 * Load the properties that control various tests from the
	 * testing.properties file on the code path
	 *
	 * @return <code>Properties</code> class with the test values
	 * @throws TestingUtilsException
	 */
	public Properties getTestProperties() throws TestingUtilsException {
		ClassLoader loader = this.getClass().getClassLoader();
		InputStream in = loader.getResourceAsStream("test-dataone.properties");
		Properties properties = new Properties();

		try {
			properties.load(in);
		} catch (IOException ioe) {
			throw new TestingUtilsException(
					"error loading test-dataone properties", ioe);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		return properties;
	}

	/**
	 * Get the given property as a boolean
	 *
	 * @param testingProperties
	 * @param key
	 * @return
	 */
	public boolean getPropertyValueAsBoolean(
			final Properties testingProperties, final String key) {
		String val = (String) testingProperties.get(key);
		if (val == null) {
			return false;
		} else {
			return Boolean.parseBoolean(val);
		}
	}

}
