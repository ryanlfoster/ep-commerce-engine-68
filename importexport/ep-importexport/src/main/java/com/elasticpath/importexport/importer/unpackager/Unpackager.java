package com.elasticpath.importexport.importer.unpackager;

import java.io.InputStream;

/**
 * Unpackager extracts input streams with separate DTO represented by XML from package.
 */
public interface Unpackager {

	/**
	 * Gets next entry from the package.
	 *
	 * @return stream for the next entry in the package
	 */
	InputStream nextEntry();
		
	/**
	 * Indicates whether nextEntry exists or not.
	 *
	 * @return true if there is next entry 
	 */
	boolean hasNext();
}
