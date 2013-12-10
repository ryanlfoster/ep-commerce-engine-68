package com.elasticpath.importexport.importer.unpackager.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;

/**
 * Unpacks ZIP file and gets input streams for every entry.
 */
public class ZipUnpackagerImpl extends AbstractUnpackagerImpl {

	private static final Logger LOG = Logger.getLogger(ZipUnpackagerImpl.class);
	
	private ZipFile zipFile;

	private Queue<String> queue;

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ImportRuntimeException in case of:
	 * <li>There are no more entries</li>
	 * <li>There is not existed entries</li>
	 * <li>Other IO problems</li>
	 * 
	 */
	public InputStream nextEntry() {
		String entryName = queue.poll();
		
		if (entryName == null) {
			throw new ImportRuntimeException("IE-30214");
		}
		
		LOG.info("Processing unpackager entry name: " + entryName);

		ZipEntry zipEntry = zipFile.getEntry(entryName);
		
		if (zipEntry == null) {
			throw new ImportRuntimeException("IE-30214", entryName);
		}

		try {
			return zipFile.getInputStream(zipEntry);
		} catch (IOException e) {
			throw new ImportRuntimeException("IE-30213", e, entryName, zipFile.getName());
		}
	}

	@Override
	public boolean hasNext() {
		if (queue.isEmpty()) {
			try {
				zipFile.close();
			} catch (IOException e) {
				throw new ImportRuntimeException("IE-30212", e, zipFile.getName());
			}
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ImportRuntimeException if there is some IO problems
	 */
	@Override
	public void initialize(final RetrievalMethod retrievalMethod) {
		try {
			File file = retrievalMethod.retrieve();
			if (!file.exists()) {
				LOG.error("The supplied file does not exist: " + file.getAbsolutePath());
			}
			zipFile = new ZipFile(file);

			ZipEntry zipEntry = zipFile.getEntry(Manifest.MANIFEST_XML);
			if (zipEntry == null) {
				throw new ImportRuntimeException("IE-30210");
			}

			Manifest manifest = new XMLUnmarshaller(Manifest.class).unmarshall(zipFile.getInputStream(zipEntry));

			queue = new LinkedList<String>(manifest.getResources());

		} catch (IOException e) {
			throw new ImportRuntimeException("IE-30216", e, retrievalMethod.retrieve().getAbsolutePath());
		}
	}
}
