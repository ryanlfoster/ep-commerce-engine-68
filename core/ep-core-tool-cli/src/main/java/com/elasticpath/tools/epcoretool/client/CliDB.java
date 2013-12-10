package com.elasticpath.tools.epcoretool.client;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.epcoretool.LoggerFacade;

/**
 * This class is used for managing the database connection and logger facade.
 */
public class CliDB {

	private static final Logger LOG = LoggerFactory.getLogger(CliDB.class);

	private final Properties configProperties;

	private final LoggerFacade logger = new LoggerFacade() {

		@Override
		public void error(final String message) {
			LOG.error(message);
		}

		@Override
		public void warn(final String message) {
			LOG.warn(message);
		}

		@Override
		public void info(final String message) {
			LOG.info(message);
		}

		@Override
		public void debug(final String message) {
			LOG.debug(message);
		}
	};

	/**
	 * Open a specific text file containing database parameters, and populate a corresponding Properties object.
	 */
	protected CliDB() {
		configProperties = LoadPropertiesHelper.loadProperties("epcoretool.config");
	}

	/**
	 * Gets the config properties.
	 * 
	 * @return the config properties
	 */
	protected Properties getConfigProperties() {
		return configProperties;
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	protected String getUrl() {
		return configProperties.getProperty("db.connection.url");
	}

	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	protected String getUsername() {
		return configProperties.getProperty("db.connection.username");
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	protected String getPassword() {
		return configProperties.getProperty("db.connection.password");
	}

	/**
	 * Gets the driver class.
	 * 
	 * @return the driver class
	 */
	protected String getDriverClass() {
		return configProperties.getProperty("db.connection.driver_class");
	}

	/**
	 * Gets the min idle.
	 * 
	 * @return the min idle
	 */
	protected Integer getMinIdle() {
		// TODO: Exception Handling for non parsable data?
		return Integer.parseInt(configProperties.getProperty("db.connection.min.idle"));
	}

	/**
	 * Gets the max idle.
	 * 
	 * @return the max idle
	 */
	protected Integer getMaxIdle() {
		// TODO: Exception Handling for non parsable data?
		return Integer.parseInt(configProperties.getProperty("db.connection.max.idle"));
	}

	/**
	 * Gets the logger facade.
	 *
	 * @return the logger facade
	 */
	protected LoggerFacade getLoggerFacade() {
		return logger;
	}
}
