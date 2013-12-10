/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.configuration.dao.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;
import com.elasticpath.tools.sync.configuration.dao.ConnectionConfigurationDao;
import com.elasticpath.tools.sync.configuration.marshal.XMLUnmarshaller;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * A DAO for loading connection configurations.
 */
public class ConnectionConfigurationDaoImpl implements ConnectionConfigurationDao {

	private static final String CONNECTION_CONFIG_SCHEMA = "schema/connectionSchema.xsd";
	
	private static final Logger LOG = Logger.getLogger(ConnectionConfigurationDaoImpl.class);

	/**
	 * Loads a connection configuration by its configuration ID.
	 * <p>
	 * Note: in current implementation it is the file name of the configuration.
	 * 
	 * @param configurationId the configuration ID
	 * @return the connection configuration
	 */
	public ConnectionConfiguration load(final String configurationId) {
		return loadConnectionConfiguration(configurationId);
	}

	private ConnectionConfiguration loadConnectionConfiguration(final String fileName) {
		try {
			final FileInputStream fileInputStream = new FileInputStream(fileName);
			try {
				return createUnmarshaler().unmarshall(fileInputStream);
			} finally {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					LOG.warn("Could not close file : " + fileName);
				}
			}
		} catch (FileNotFoundException e) {
			throw new SyncToolConfigurationException("Could not load connection configuation", e);
		}
	}

	/**
	 * Creates XMLUnmarshaller.
	 * 
	 * @return XMLUnmarshaller
	 */
	private static XMLUnmarshaller createUnmarshaler() {
		final XMLUnmarshaller unmarshaller = new XMLUnmarshaller(ConnectionConfiguration.class);
		ValidationEventHandler validationEventHandler = new DefaultValidationEventHandler();
		unmarshaller.initValidationParameters(CONNECTION_CONFIG_SCHEMA, validationEventHandler);
		return unmarshaller;
	}

}
