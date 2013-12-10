package com.elasticpath.importexport.importer.unpackager;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;

/**
 * Creates and initializes ready to use unpackagers.
 */
public interface UnpackagerFactory {

	/**
	 * Creates Unpackager by package type and initialize it with retrieval method.
	 * 
	 * @param packageType type of package used to create unpackager
	 * @param retrievalMethod the retrieval method to initialize unpackager with
	 * @throws ConfigurationException in case unpackager hasn't been created properly 
	 * @return ready to use unpackager
	 */
	Unpackager createUnpackager(final PackageType packageType, final RetrievalMethod retrievalMethod) throws ConfigurationException;
}
