/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.importexport.exporter.exportentry.impl;

import java.io.InputStream;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.EntryException;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;

/**
 * Export entry that contains the fileObject and entry name of the object that
 * is to be exported.
 */
public class FileObjectExportEntry implements ExportEntry {

	private static final Logger LOG = Logger.getLogger(FileObjectExportEntry.class);
	private final FileObject file;
	private final String entryName;
	
	/**
	 * Constructor which creates an exportEntry based on the entry name
	 * and fileObject.
	 * @param entryName of the entry to be exported
	 * @param file to be exported
	 */
	public FileObjectExportEntry(final String entryName, final FileObject file) {
		this.entryName = entryName;		
		this.file = file;
	}
	
	/**
	 * Return the input stream of the file object.
	 * @return input stream
	 * @throws EntryException exception if input stream was not retrieved correctly
	 */
	public InputStream getInputStream() throws EntryException {
		if (file == null) {
			return null;
		}
		try {
			return file.getContent().getInputStream();
		} catch (FileSystemException fse) {
			throw new EntryException("IE-20100", fse, entryName);
		}
	}
	
	/**
	 * Close the file object if it is not null and the file content is open.
	 */
	public void close() {
		try {
			if (file != null && file.getContent().isOpen()) {
				file.close();
			}
		} catch (FileSystemException fse) {
			LOG.warn("Failed to close entry: " + entryName);
		}
	}
	
	/**
	 * Returns the name of the entry.
	 * @return the entry name
	 */
	public String getName() {
		return entryName;
	}
}
