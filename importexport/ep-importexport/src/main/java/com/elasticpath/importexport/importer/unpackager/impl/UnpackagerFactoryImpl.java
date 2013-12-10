package com.elasticpath.importexport.importer.unpackager.impl;

import java.util.Map;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.unpackager.Unpackager;
import com.elasticpath.importexport.importer.unpackager.UnpackagerFactory;

/**
 * Creates and initializes ready to use unpackagers.
 */
public class UnpackagerFactoryImpl implements UnpackagerFactory {

	/**
	 * Creates Unpackager depending on MethodType.
	 * 
	 * @param packageType the PackageType
	 * @param retrievalMethod the retrievalMethod
	 * @return Unpackager
	 */
	private Map<PackageType, AbstractUnpackagerImpl> packagerMap;

	/**
	 * Create Unpackager by package type and initialize it with retrieval method.
	 * 
	 * @param packageType the packager type
	 * @param retrievalMethod the retrieval method
	 * @throws ConfigurationException if could not create an Unpackager
	 * @return Unpackager
	 */
	public Unpackager createUnpackager(final PackageType packageType, final RetrievalMethod retrievalMethod) throws ConfigurationException {
		final AbstractUnpackagerImpl unpackager = packagerMap.get(packageType);

		if (unpackager == null) {
			throw new ConfigurationException("Importer for package type " + packageType + " doesn't exist");
		}

		unpackager.initialize(retrievalMethod);
		return unpackager;
	}

	/**
	 * Gets the map with available package types and associated unpackagers.
	 * 
	 * @return the packagerMap
	 */
	public Map<PackageType, AbstractUnpackagerImpl> getPackagerMap() {
		return packagerMap;
	}

	/**
	 * Sets the map with available package types and associated unpackagers.
	 * 
	 * @param packagerMap the packagerMap to set
	 */
	public void setPackagerMap(final Map<PackageType, AbstractUnpackagerImpl> packagerMap) {
		this.packagerMap = packagerMap;
	}
}
