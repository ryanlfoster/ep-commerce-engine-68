/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Reads property files from a specific location.
 * 
 * @deprecated Use {@link com.elasticpath.persistence.dao.impl.PropertiesDaoLoaderFactoryImpl}
 */
@Deprecated
public class PropertiesDaoImpl implements PropertiesDao {
	private static final Logger LOG = Logger.getLogger(PropertiesDaoImpl.class.getName());

	/** File name suffix for properties files. */
	public static final String PROPERTIES_SUFFIX = ".properties";

	private final Map<String, URL> storedProperties = new HashMap<String, URL>();

	private String propertiesLocation;

	private String storedPropertiesLocation;

	private ResourcePatternResolver resourceLoader;

	/**
	 * Go through the resources directory and load all the .properties file into the hashMap with the filename (without ".properties") as the key.
	 * For example, countries_fr_CA.properties will be loaded with key "countries_fr_CA". Returns a properties map where the keys are properties file
	 * names and the values are Properties objects. If the resources exist in the filesystem load them from there instead.
	 * 
	 * @return the Map of properties file names (without the file extension) to Properties objects
	 * @throws EpPersistenceException in case of errors
	 */
	public Map<String, Properties> loadProperties() throws EpPersistenceException {
		Map<String, Properties> propertiesHashMap = new HashMap<String, Properties>();

		Resource[] resources = null;
		Resource[] resourcesFileSystem = null;
		try {
			resources = resourceLoader.getResources(propertiesLocation + "/*");
		} catch (IOException e) {
			LOG.fatal("Unable to find resource under " + propertiesLocation + "/*", e);
			throw new EpPersistenceException("Unable to list property files for classpath entries in " + propertiesLocation + "/" + "*", e);
		}
		String propertiesFileSystemLocation = "file:" + getStoredPropertyLocation() + "/*";
		try {
			resourcesFileSystem = resourceLoader.getResources(propertiesFileSystemLocation);	
		} catch (IOException e) {
			LOG.fatal("Unable to find resource under " + propertiesFileSystemLocation, e);
			throw new EpPersistenceException("Unable to list property files for classpath entries in " + propertiesFileSystemLocation, e);
		}

		if (resources != null && resourcesFileSystem != null) {
			for (Resource resource : resources) {
				if (!resource.getFilename().endsWith(PROPERTIES_SUFFIX)) {
					continue;
				}
				Resource fileSystemResource = null;
				for (Resource resourceFileSystem : resourcesFileSystem) {
					String classpathResourceFilename = resource.getFilename();
					String filesystemResourceFilename = resourceFileSystem.getFilename();
					if (classpathResourceFilename.equals(filesystemResourceFilename)) {
						fileSystemResource = resourceFileSystem;
						break;
					}				
					
				}

                // If no property file found in the filesystem, use the default property file on the classpath
                if (fileSystemResource == null) {
                    addProperties(propertiesHashMap, resource);
                } else {
                    // Otherwise if the filesystem contains a property file with the same name as the one in the class path use it instead.
                    addProperties(propertiesHashMap, fileSystemResource);
                }
            }
		}

		return propertiesHashMap;
	}

	/**
	 * Loads the properties from the {@link Resource} given, and adds it to the map given,
	 * using the filename (without the .properties extension) as the key.
	 *
	 * @param propertiesMap the properties map to update.
	 * @param resource the resource to load the {@link Properties} object from.
	 */
	protected void addProperties(final Map<String, Properties> propertiesMap, final Resource resource) {
		final String propertyName = resource.getFilename().substring(0, resource.getFilename().lastIndexOf(PROPERTIES_SUFFIX));
		final Properties properties = getProperties(resource);

		propertiesMap.put(propertyName, properties);
		LOG.info("Loaded " + resource.toString() + " successfully...");
	}

	@SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
	private Properties getProperties(final Resource resource) {
		Properties myProperties = new Properties();
		
		if (resource == null) {
			throw new IllegalArgumentException("resource is null!");
		}
		
		InputStream stream = null;
		try {
			stream = resource.getInputStream();
			myProperties.load(stream);
		} catch (final IOException e) {
			throw new EpPersistenceException("Failed to load properties file: " + resource, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new EpSystemException("IO Exception", e);
				}
			}
		}
		return myProperties;
	}

	@Override
	public void setPropertiesLocation(final String propertiesLocation) {
		this.propertiesLocation = propertiesLocation;
	}

	@Override
	public void setStoredPropertiesLocation(final String storedPropertiesLocation) {
		this.storedPropertiesLocation = storedPropertiesLocation;
	}

	@Override
	public Properties getPropertiesFile(final String propertyFile) {
		String fileName = addSuffixIfNecessary(propertyFile);

		// stored properties override initial properties
		if (storedProperties.containsKey(fileName)) {
			return getProperties(resourceLoader.getResource(storedProperties.get(fileName).toString()));
		}

		/* 
		 * It doesn't make sense to search classpath*: for a single resource, but we have to support
		 * it for #loadProperties(). Convert to classpath:
		 */
		String propertiesLocation = this.propertiesLocation;
		if (propertiesLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
			propertiesLocation = String.format("%s%s", ResourceLoader.CLASSPATH_URL_PREFIX, propertiesLocation
					.substring(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX.length()));
		}
		String propertiesFileSetLocation =  "file:" + getStoredPropertyLocation();
		Resource resourceFileSet = resourceLoader.getResource(String.format("%s%s%s", propertiesFileSetLocation, "/", fileName));
		if (resourceFileSet != null && resourceFileSet.exists()) {
			return getProperties(resourceFileSet);
		} 
		
		return getProperties(resourceLoader.getResource(String.format("%s/%s", propertiesLocation, fileName)));
	}

	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	private String getStoredPropertyLocation() {
		
		if (storedPropertiesLocation == null) {
//			storedPropertiesLocation = String.format("%s%2$sconf%2$sresources", elasticPath.getWebInfPath(), File.separator);
			storedPropertiesLocation = ElasticPathImpl.getInstance().getWebInfPath().replace('\\', '/') + "/conf/resources";

		}
		return storedPropertiesLocation;
	}

	/**
	 * Persist the properties object to file, takes filename with properties extension or without extension.
	 * 
	 * @param property the properties object to store to file
	 * @param propertyFileName the filename for the properties
	 */
	public void storePropertiesFile(final Properties property, final String propertyFileName) {

		try {
			String fileName = addSuffixIfNecessary(propertyFileName);
			File file = new File(getStoredPropertyLocation() + "/" + fileName);
			file.getParentFile().mkdirs();
			final FileOutputStream fileOutputStream = new FileOutputStream(file);
			property.store(fileOutputStream, null);
			fileOutputStream.close();
			try {
				storedProperties.put(fileName, file.toURI().toURL());
			} catch (MalformedURLException e) {
				// should never happen otherwise writing should have failed sooner
				throw new EpPersistenceException("Unable to find property file after writing: " + propertyFileName, e);
			}
		} catch (final IOException e) {
			throw new EpPersistenceException("Failed to store properties file: " + propertyFileName, e);
		}
	}

	private String addSuffixIfNecessary(final String fileName) {
		String result = fileName;
		if (result.lastIndexOf(PROPERTIES_SUFFIX) == -1) {
			result = result + PROPERTIES_SUFFIX;
		}
		return result;
	}

	/**
	 * Sets the {@link ResourcePatternResolver} instance to use.
	 * 
	 * @param resourceLoader {@link ResourcePatternResolver} instance to use
	 */
	public void setResourceLoader(final ResourcePatternResolver resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
