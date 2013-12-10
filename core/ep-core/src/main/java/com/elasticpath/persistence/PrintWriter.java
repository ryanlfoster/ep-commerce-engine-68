package com.elasticpath.persistence;

import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * Represents a print writer.
 */
public interface PrintWriter {

	/**
	 * Open a file to write.
	 * 
	 * @param fileName
	 *            the file name
	 * @throws EpPersistenceException
	 *             if any error happens
	 */
	void open(String fileName) throws EpPersistenceException;
	
	/**
	 * Writes the given string as a line.
	 * @param string the string to write
	 * @throws EpPersistenceException in case any error happens
	 */
	void println(String string) throws EpPersistenceException;
	
	/**
	 * Close the file.
	 * 
	 * @throws EpPersistenceException
	 *             in case of any IO error happens
	 */
	void close() throws EpPersistenceException;	
}
