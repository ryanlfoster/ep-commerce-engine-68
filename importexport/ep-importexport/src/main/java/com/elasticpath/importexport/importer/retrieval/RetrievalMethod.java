package com.elasticpath.importexport.importer.retrieval;

import java.io.File;

/**
 * Retrieve streams to be imported from the source: file system, FTP server etc.  
 */
public interface RetrievalMethod {
	
	/**
	 * Retrieve next file.
	 *
	 * @return retrieved File.
	 */
	File retrieve();
}
