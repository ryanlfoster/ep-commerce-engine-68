package com.elasticpath.service.remote.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.ElasticPathServiceImpl;
import com.elasticpath.service.remote.ResourceRetrievalService;

/**
 * Provides the contents of various configuration files as Strings.
 */
public class ResourceRetrievalServiceImpl extends ElasticPathServiceImpl implements ResourceRetrievalService {
	private static final Logger LOG = Logger.getLogger(ResourceRetrievalService.class);
	
	private static final String DEFAULT_SEARCH_CONFIG_FILE_NAME = "search-config.xml";
	private String searchConfigFileName = DEFAULT_SEARCH_CONFIG_FILE_NAME;
	
	@Override
	public String getSearchConfig() throws EpServiceException {
		String filePath = getElasticPath().getWebInfPath() + File.separator + this.searchConfigFileName;
		return fileToString(filePath);
	}
	
	@SuppressWarnings("PMD.EmptyCatchBlock")
	private String fileToString(final String filePath) {
		LOG.debug("reading in file at " + filePath);
		File file = new File(filePath);
		InputStream inputStream = null;
		byte[] bytes;
		try {
			inputStream = new FileInputStream(file);
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				throw new EpServiceException("File at " + filePath + " is too large");
			}
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && numRead >= 0) {
				numRead = inputStream.read(bytes, offset, bytes.length - offset);
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new EpServiceException("Could not completely read file at " + filePath);
			}
		} catch (IOException e) {
			throw new EpServiceException("Unable to open configuration file at " + filePath, e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				//ignore
			}
		}
		
		return new String(bytes);
	}
	
	/**
	 * Sets the name of the search configuration file.
	 * Defaults to "search-config.xml" if not called.
	 * @param filename the name of the search configuration file.
	 */
	public void setSearchConfigFileName(final String filename) {
		this.searchConfigFileName = filename;
	}
	
    @Override

    public Map<String, Properties> getProperties() throws EpServiceException {
          final PropertiesDao propertiesDao = getBean(ContextIdNames.PROPERTIES_DAO);
          return propertiesDao.loadProperties();
    }

	
}
