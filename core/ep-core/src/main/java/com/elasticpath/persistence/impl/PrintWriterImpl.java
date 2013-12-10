package com.elasticpath.persistence.impl;

import java.io.FileWriter;
import java.io.IOException;

import com.elasticpath.persistence.PrintWriter;
import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * This is a default implementation of <code>PrintWriter</code>. It's just a wrapper of <code>java.io.PrintWriter</code>.
 */
public class PrintWriterImpl implements PrintWriter {

	private java.io.PrintWriter printWriter;

	/**
	 * Open a file to write.
	 * 
	 * @param fileName the file name
	 * @throws EpPersistenceException if any error happens
	 */
	public void open(final String fileName) throws EpPersistenceException {
		try {
			this.printWriter = new java.io.PrintWriter(new FileWriter(fileName));
		} catch (final IOException e) {
			throw new EpPersistenceException("Cannot open file.", e);
		}
	}

	/**
	 * Writes the given string as a line.
	 * 
	 * @param string the string to write
	 * @throws EpPersistenceException in case any error happens
	 */
	public void println(final String string) throws EpPersistenceException {
		this.printWriter.println(string);
	}

	/**
	 * Close the file.
	 * 
	 * @throws EpPersistenceException in case of any IO error happens
	 */
	public void close() throws EpPersistenceException {
		this.printWriter.close();
	}
}
