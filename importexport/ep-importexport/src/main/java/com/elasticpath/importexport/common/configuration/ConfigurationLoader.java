package com.elasticpath.importexport.common.configuration;

import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.ValidationEventHandler;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.MarshallingRuntimeException;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;

/**
 * Loads and validates export configuration.
 * 
 */
public class ConfigurationLoader {
	
	private Map<String, String> schemaPathMap;
	
	private ValidationEventHandler validationEventHandler;

	/**
	 * Loads the configuration from an input stream.
	 *
	 * @param configurationStream stream containing the configuration
	 * @param configClass class of the Config type
	 * @param <T> the configuration type
	 * @throws ConfigurationException in case configuration is invalid
	 * @return valid ExportConfiguration
	 */
	public <T> T load(final InputStream configurationStream, final Class<T> configClass) throws ConfigurationException {
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller(configClass);
		unmarshaller.initValidationParameters(schemaPathMap.get(configClass.getSimpleName()), validationEventHandler);
		try {
			return unmarshaller.unmarshall(configurationStream);
		} catch (MarshallingRuntimeException e) {
			throw new ConfigurationException("Configuration error: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets the XML schema path Map between class name and path to XML schema for XML configuration validation.
	 * 
	 * @return XML schema file path map
	 */
	public Map<String, String> getSchemaPathMap() {
		return schemaPathMap;
	}

	/**
	 * Sets XML schema path Map between class name and path to XML schema to validate XML configuration with.
	 * 
	 * @param schemaPathMap XML schema path map
	 */
	public void setSchemaPathMap(final Map<String, String> schemaPathMap) {
		this.schemaPathMap = schemaPathMap;
	}
	
	/**
	 * Gets validation event hander.
	 * 
	 * @return validationEventHandler
	 */
	public ValidationEventHandler getValidationEventHandler() {
		return validationEventHandler;
	}

	/**
	 * Sets validation event handler to validate XML by.
	 * 
	 * @param validationEventHandler validation event handler
	 */
	public void setValidationEventHandler(final ValidationEventHandler validationEventHandler) {
		this.validationEventHandler = validationEventHandler;
	}

}
