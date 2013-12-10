package com.elasticpath.importexport.exporter.packager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;

/**
 * Pack entries into ZIP archive. 
 */
public class ZipPackagerImpl extends AbstractPackagerImpl {
	
	private static final Logger LOG = Logger.getLogger(ZipPackagerImpl.class);
	
	private static final int BUFFER_SIZE = 1024;

	private ZipOutputStream zipOutput;
	private Set<String> entries;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ExportRuntimeException if packageName is not specified 
	 */
	public void initialize(final DeliveryMethod deliveryMethod, final String packageName) {
		if (packageName == null) {
			throw new ExportRuntimeException("IE-20200");
		}
		
		zipOutput = new ZipOutputStream(deliveryMethod.deliver(packageName));
		entries = new HashSet<String>();
	}
	
	/**
	 * Place new entry into the package based on ZIP output stream.
	 * 
	 * @param entry input stream containing entry to be added
	 * @throws ExportRuntimeException if stream processing fails 
	 * @param fileName name for new entry in ZIP archive
	 */
	public void addEntry(final InputStream entry, final String fileName) {
		LOG.debug("Start adding entry " + fileName + " to zip");
		if (entries.contains(fileName)) {
			LOG.debug("entry already in the archive. skipping.");
			return;
		}
		entries.add(fileName);
		try {
			zipOutput.putNextEntry(new ZipEntry(fileName));
			byte[] bytes = new byte[BUFFER_SIZE];
			int numRead = 0;
			while ((numRead = entry.read(bytes, 0, BUFFER_SIZE)) != -1) {
				zipOutput.write(bytes, 0, numRead);
			}
			zipOutput.closeEntry();
		} catch (IOException e) {
			throw new ExportRuntimeException("IE-20201", e, fileName);
		}
		LOG.debug("Finish adding entry " + fileName + " to zip");
	}

	/**
	 * Close ZIP output stream.
	 */
	public void finish() {
		try {
			zipOutput.close();
		} catch (IOException e) {
			LOG.error("Could not close the stream", e);
		}
	}
}
