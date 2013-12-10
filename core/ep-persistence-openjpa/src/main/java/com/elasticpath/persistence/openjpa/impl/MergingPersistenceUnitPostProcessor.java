/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.persistence.openjpa.impl;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A persistence unit post processor which merges multiple persistence units with the
 * same name within the same classloader.  Exclusions can also be provided to allow disabling
 * of existing configuration without having to change the source for binary packages.
 */
public class MergingPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

	private final Map<String, List<String>> puiClasses = new  HashMap<String, List<String>>();
    private final Map<String, List<URL>> puiURLs = new HashMap<String, List<URL>>();
    private final Map<String, List<String>> puiMappings = new HashMap<String, List<String>>();
	private final Map<String, Properties> puiProperties = new HashMap<String, Properties>();
	private List<String> mappingFilesExclusions = new ArrayList<String>();
	private List<String> managedClassNameExclusions = new ArrayList<String>();
	private List<URL> jarFileUrlExclusions = new ArrayList<URL>();


	/**
	 * Post-process the given PersistenceUnitInfo to perform the merge.
	 *
	 * @param persistenceUnitInfo the persistence unit info
	 */
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo persistenceUnitInfo) {
        mergeManagedClassNames(persistenceUnitInfo);
        mergeJarFileUrls(persistenceUnitInfo);
        mergeMappingFileNames(persistenceUnitInfo);
        mergeProperties(persistenceUnitInfo);
	}

	/**
	 * Merge the collection of managed class names from the given unit with any other managed class
	 * names from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeManagedClassNames(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		List<String> classes = puiClasses.get(persistenceUnitInfo.getPersistenceUnitName());
		if  (classes ==  null) {
			classes = new  ArrayList<String>();
			puiClasses.put(persistenceUnitInfo.getPersistenceUnitName(), classes);
		}
		final  List<String> names = persistenceUnitInfo.getManagedClassNames();
		names.removeAll(managedClassNameExclusions);
		classes.addAll(names);
		persistenceUnitInfo.getManagedClassNames().clear();
		persistenceUnitInfo.getManagedClassNames().addAll(classes);
	}

	/**
	 * Merge the collection of jar file urls from the given unit with any other urls
	 * from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeJarFileUrls(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		List<URL> urls = puiURLs.get(persistenceUnitInfo.getPersistenceUnitName());
		if (urls == null) {
			urls = new ArrayList<URL>();
			puiURLs.put(persistenceUnitInfo.getPersistenceUnitName(), urls);
		}
		final List<URL> nameUrls = persistenceUnitInfo.getJarFileUrls();
		nameUrls.removeAll(jarFileUrlExclusions);
		urls.addAll(nameUrls);
		persistenceUnitInfo.getJarFileUrls().clear();
		persistenceUnitInfo.getJarFileUrls().addAll(urls);
	}

	/**
	 * Merge the collection of mapping file names from the given unit with any other mapping file
	 * names from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	protected void mergeMappingFileNames(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		List<String> mappings = puiMappings.get(persistenceUnitInfo.getPersistenceUnitName());
        if (mappings == null) {
            mappings = new ArrayList<String>();
            puiMappings.put(persistenceUnitInfo.getPersistenceUnitName(), mappings);
        }
        final List<String> nameMappings = persistenceUnitInfo.getMappingFileNames();
        nameMappings.removeAll(mappingFilesExclusions);
        mappings.addAll(nameMappings);
        persistenceUnitInfo.getMappingFileNames().clear();
        persistenceUnitInfo.getMappingFileNames().addAll(mappings);
	}


	/**
	 * Merge the properties from the given unit with any other properties
	 * from other files with the same unit name.
	 * 
	 * @param persistenceUnitInfo the persistence unit to merge
	 */
	private void mergeProperties(final MutablePersistenceUnitInfo persistenceUnitInfo) {
		Properties properties = puiProperties.get(persistenceUnitInfo.getPersistenceUnitName());
		if (properties == null) {
			properties = new Properties();
			puiProperties.put(persistenceUnitInfo.getPersistenceUnitName(), properties);
		}
		final Properties props = persistenceUnitInfo.getProperties();
		properties.putAll(props);
		persistenceUnitInfo.getProperties().putAll(properties);
	}

	/**
	 * These file names will be excluded from the merged persistence configuration and therefore
	 * that file's named queries and entity definitions will not be included.
	 * @param exclusions the names of the files to exclude.
	 */
	public void setExcludedMappingFiles(final List<String> exclusions) {
		this.mappingFilesExclusions = exclusions;
	}

	/**
	 * These managed class names will be excluded from the merged configuration.
	 * @param exclusions the names of the classes to exclude.
	 */
	public void setExcludedManagedClassNames(final List<String> exclusions) {
		this.managedClassNameExclusions = exclusions;
	}

	/**
	 * These jar files will be excluded from the merged configuration meaning the 
	 * persistence elements in those files won't be loaded.
	 * @param exclusions the urls to exclude.
	 */
	public void setExcludedJarFileUrls(final List<URL> exclusions) {
		this.jarFileUrlExclusions = exclusions;
	}

}

