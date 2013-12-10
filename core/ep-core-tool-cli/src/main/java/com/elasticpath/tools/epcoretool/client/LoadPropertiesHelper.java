package com.elasticpath.tools.epcoretool.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LoadPropertiesHelper is used to load properties files.
 */
public final class LoadPropertiesHelper {
	private static final Logger LOG = LoggerFactory.getLogger(LoadPropertiesHelper.class);

	private LoadPropertiesHelper() {
		// Do not allow this class to be instantiated directly.
	}
	
	/**
	 * Load properties.
	 *
	 * @param filename the filename
	 * @return the properties
	 */
	public static Properties loadProperties(final String filename) {
		if (filename == null) {
			throw new IllegalArgumentException("Filename cannot be null.");
		}

		Properties result = new Properties();

		InputStream input = null;
		try {
			input = Cli.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				throw new IOException(String.format("Unable to locate %s on the classpath.", filename));
			}
			result.load(input);

		} catch (final IOException ex) {
			LOG.error(String.format("Cannot open and load %s file.", filename), ex);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (final IOException ex) {
				LOG.error(String.format("Cannot close %s file.", filename), ex);
			}
		}

		return result;
	}
}
