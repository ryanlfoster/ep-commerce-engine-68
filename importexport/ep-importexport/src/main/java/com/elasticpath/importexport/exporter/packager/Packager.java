package com.elasticpath.importexport.exporter.packager;

import java.io.InputStream;

/**
 * Packaging is the step of export process occurs after transformations and before delivering. 
 */
public interface Packager {
	
	/**
	 * Pack new entry.
	 *
	 * @param entry input stream containing entry to be added
	 * @param fileName name for new entry
	 */
	void addEntry(final InputStream entry, final String fileName);
	
	/**
	 * Notify that all entries are packed.
	 */
	void finish();
}
