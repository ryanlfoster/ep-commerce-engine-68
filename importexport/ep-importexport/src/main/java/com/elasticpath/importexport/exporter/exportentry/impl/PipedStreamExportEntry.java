/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.importexport.exporter.exportentry.impl;

import java.io.InputStream;

import com.elasticpath.importexport.common.exception.runtime.EntryException;
import com.elasticpath.importexport.common.util.runner.PipedStreamRunner;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;

/**
 * PipedStreamExportEntry export entry, used to access the input
 * stream connected with the processed output stream by the pipe.
 */
public class PipedStreamExportEntry implements ExportEntry {
	
	private final PipedStreamRunner pipedStreamRunner;
	private final String entryName;
	
	/**
	 * Create a PipedStreamExportEntry using an entry name and a piped stream runner.
	 * @param entryName name of the entry
	 * @param pipedStreamRunner piped stream runner
	 */
	public PipedStreamExportEntry(final String entryName, final PipedStreamRunner pipedStreamRunner) {
		this.pipedStreamRunner = pipedStreamRunner;
		this.entryName = entryName;
	}
	
	/**
	 * Close which does not do anything in the PipedStreamExportEntry since
	 * a piped stream runner is not able to be closed.
	 */
	public void close() {
		// piped stream runners cannot be closed.
	}

	/**
	 * Returns the result stream from the piped stream runner.
	 * @return result input stream from the piped stream runner
	 * @throws EntryException exception if failed
	 */
	public InputStream getInputStream() throws EntryException {
		return pipedStreamRunner.createResultStream();
	}
	

	/**
	 * Returns the name of the entry.
	 * @return name of the entry
	 */
	public String getName() {
		return entryName;
	}
}