/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.importexport.exporter.exportentry;

import java.io.InputStream;


import com.elasticpath.importexport.common.exception.runtime.EntryException;

/**
 * Export entry that contains the fileObject and entry name of the object that
 * is to be exported.
 */
public interface ExportEntry {

	/**
	 * Get the input stream of the fileObject. 
	 * @return input stream of the file object
	 * @throws EntryException exception if the input stream is unable to be obtained
	 */
	InputStream getInputStream() throws EntryException;
	
	/**
	 * Close the fileObject after the entry has been exported.
	 */
	void close();
	
	/**
	 * Return the entry name.
	 * @return the entry name
	 */
	String getName();
	
}
