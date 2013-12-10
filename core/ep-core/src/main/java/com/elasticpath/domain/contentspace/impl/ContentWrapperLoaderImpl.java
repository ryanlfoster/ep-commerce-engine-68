/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperLoader;

/**
 * A class that loads all content wrappers from the content wrappers directory in the assets location.
 */
public class ContentWrapperLoaderImpl implements ContentWrapperLoader {
	
	/**
	 * The path of the setting for the asset location.
	 */
	private static final Logger LOG = Logger.getLogger(ContentWrapperLoaderImpl.class);

	private AssetRepository assetRepository;
	
	private BeanFactory beanFactory;
	
	/**
	 * The schema language to use for validation.
	 */
	public static final String SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * The content wrapper XSD schema file name to use for validation.
	 */
	public static final String SCHEMA_FILENAME = "contentWrapper.xsd";
	
	/**
	 * Loads the content wrappers from the content wrappers directory in the assets folder,
	 * each *.xml is parsed in order to populate the content wrappers for the repository.
	 * 
	 * @return a map of wrapper Id linked to content wrappers that have been loaded from XML files
	 */
	public Map<String, ContentWrapper> loadContentWrappers() {
		
		Map<String, ContentWrapper> contentWrappers = new ConcurrentHashMap<String, ContentWrapper>();
		Collection<File> wrapperFilePaths = getContentWrapperFilePaths();
		
		// Create an unmarshaller for the XML parsing
		Unmarshaller unmarshaller;		
		try {
			unmarshaller = JAXBContext.newInstance(ContentWrapperImpl.class).createUnmarshaller();
		} catch (JAXBException e) {
			LOG.error("Failed to create JAXB unmarshaller of content wrapper XML files.", e);
			return contentWrappers;
		}
		
		// Set the XML Schema validation at unmarshal time
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(SCHEMA_LANGUAGE);
			Schema schema = schemaFactory.newSchema(getSchemaFile());
			unmarshaller.setSchema(schema);
		} catch (Exception e) {
			LOG.error("Failed to create schema validator of content wrapper XML files.", e);
			return contentWrappers;
		}
		
		// Unmarshal each XML file and add the content wrapper to the map
		for (File file : wrapperFilePaths) {
			ContentWrapper wrapper = loadContentWrapperXmlFile(unmarshaller, file);
			
			if (StringUtils.isEmpty(wrapper.getTemplateName()) || StringUtils.isEmpty(wrapper.getWrapperId())) {
				LOG.warn("Unable to loaded content wrapper, either template name or wrapper Id were null.");
			} else {

				//Make sure that wrappers have unique Id's
				if (contentWrappers.containsKey(wrapper.getWrapperId())) {
					LOG.warn("Duplicate content wrapper Id exists, wrapper not loaded: " + wrapper.getWrapperId());
				} else {
					contentWrappers.put(wrapper.getWrapperId(), wrapper);
				}
			}
		}
		
		return contentWrappers;
	}

	/**
	 * Loads an XML file and parses the information and fills the content wrapper object.
	 * @param unmarshaller the unmarshaller to be used
	 * @param file to be parsed for content wrapper data
	 * @return content wrapper 
	 */
	private ContentWrapper loadContentWrapperXmlFile(final Unmarshaller unmarshaller, final File file) {
		ContentWrapper wrapper = beanFactory.getBean(ContextIdNames.CONTENT_WRAPPER);

		try {
			wrapper = (ContentWrapper) unmarshaller.unmarshal(file);
		} catch (JAXBException e) {
			LOG.error("Failed to unmarshal content wrapper XML: " + file.getAbsolutePath(), e);
		}
		return wrapper;
	}

	/**
	 * Helper method that returns a list of eligible content wrapper xml files.
	 *
	 * @return a collection of xml files in the content wrapper directory
	 */
	@SuppressWarnings("unchecked")
	private Collection<File> getContentWrapperFilePaths() {
		Collection<File> cwFiles = new ArrayList<File>();
		
		String cwLocation = getContentWrapperDirectoryLocation();
		LOG.info("Loading content wrappers: " + cwLocation);
		if (cwLocation == null) {
			LOG.error("The assets location is invalid, please check to ensure your asset location setting is correct.");
			
			return cwFiles;
		}
		
		final File contentWrapperDir = new File(cwLocation);
		if (!(contentWrapperDir.exists() && contentWrapperDir.canRead())) {
			LOG.error("Content wrapper directory not found or not readable.");
			
			return cwFiles;
		}
		
		//TODO: Need to enable recursive scanning of CW directory
		boolean recursive = false;
		cwFiles = FileUtils.listFiles(contentWrapperDir, new String[] {"xml"}, recursive);
		
		if (cwFiles == null || cwFiles.isEmpty()) {
			LOG.info("No content wrapper XML files found: " + cwLocation);
			
			return new ArrayList<File>();
		}
		
		return cwFiles;
	}

	/**
	 * Method for obtaining the value of the asset location setting.
	 * @return the location of the assets folder
	 */
	protected String getContentWrapperDirectoryLocation() {
		
		return getAssetRepository().getContentWrappersPath();
		
	}
	
	/**
	 * Set the bean factory for the content wrapper loader.
	 * 
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Returns a file handle to the XML Schema document.
	 * 
	 * @return The XSD schema file.
	 */
	protected File getSchemaFile() {
		String dir = getContentWrapperDirectoryLocation();
		File schemaFile = new File(dir, SCHEMA_FILENAME);
		
		if (!schemaFile.exists()) {
			LOG.warn("Content wrapper schema file not found: " + schemaFile.getAbsolutePath());
		}
		
		return schemaFile;
	}

	/**
	 * @return asset repository, which will be used to resolve the full path to content wrappers directory.
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * set asset repository, which will be used to resolve the full path to content wrappers directory.
	 *
	 * @param assetRepository asset repository
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
	
}
