package com.elasticpath.importexport.exporter.packager.impl;

import java.util.Map;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;
import com.elasticpath.importexport.exporter.packager.Packager;
import com.elasticpath.importexport.exporter.packager.PackagerFactory;

/**
 * Packager Factory implementation contains the map of available packagers.
 * The map is injected by Spring so available packagers can be easily configured
 */
public class PackagerFactoryImpl implements PackagerFactory {

	private Map<PackageType, AbstractPackagerImpl> packagerMap;

	/**
	 * Create packager by package type and initialize it with delivery method and package name option.
	 *
	 * @param packagerConfiguration contains type of packager to be created and package file name option
	 * @param deliveryMethod delivery method used for delivering of produced package 
	 * @throws ConfigurationException if packager of the given type hasn't been found in the map 
	 * @return configured and ready to use packager
	 */
	public Packager createPackager(final PackagerConfiguration packagerConfiguration,
			final DeliveryMethod deliveryMethod) throws ConfigurationException {
		final AbstractPackagerImpl packager = packagerMap.get(packagerConfiguration.getType());

		if (packager == null) {
			throw new ConfigurationException("Packager for " + packagerConfiguration.getType().toString() + " doesn't exist");
		}

		packager.initialize(deliveryMethod, packagerConfiguration.getPackageName());
		return packager;
	}

	/**
	 * Gets the map of available packagers.
	 * 
	 * @return the packagerMap
	 */
	public Map<PackageType, AbstractPackagerImpl> getPackagerMap() {
		return packagerMap;
	}

	/**
	 * Sets the map of available packagers.
	 * 
	 * @param packagerMap the packagerMap to set
	 */
	public void setPackagerMap(final Map<PackageType, AbstractPackagerImpl> packagerMap) {
		this.packagerMap = packagerMap;
	}
}
