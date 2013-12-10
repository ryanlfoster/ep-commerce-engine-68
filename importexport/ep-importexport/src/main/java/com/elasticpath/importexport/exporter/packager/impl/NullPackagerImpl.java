package com.elasticpath.importexport.exporter.packager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;

/**
 * Null packager doesn't provide any packaging and just send each file to delivery method separately. 
 */
public class NullPackagerImpl extends AbstractPackagerImpl {
	
	private static final Logger LOG = Logger.getLogger(NullPackagerImpl.class);
	
	private static final int BUFFER_SIZE = 1024;
	
	private DeliveryMethod deliveryMethod;

	/**
	 * Saves delivery method for future using but doesn't start delivering immediatelly.
	 *
	 * @param deliveryMethod delivery method to deliver produced package
	 * @param packageName default name of produced file isn't used in this packager
	 */
	public void initialize(final DeliveryMethod deliveryMethod, final String packageName) {
		this.deliveryMethod = deliveryMethod;
	}

	/**
	 * Pass next stream to delivery method without any packaging.
	 *
	 * @param entry input stream to be added
	 * @throws ExportRuntimeException if stream processing fails
	 * @param fileName the name of next package which will be passed to delivery method
	 */
	public void addEntry(final InputStream entry, final String fileName) {
		LOG.debug("Start passing stream " + fileName + " to delivery method");
		final OutputStream outputStream = deliveryMethod.deliver(fileName);
		byte[] bytes = new byte[BUFFER_SIZE];
		try {
			int numRead = 0;
			while ((numRead = entry.read(bytes, 0, BUFFER_SIZE)) != -1) {
				outputStream.write(bytes, 0, numRead);
			}
		} catch (IOException e) {
			throw new ExportRuntimeException("IE-20201", e, fileName);
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				LOG.error("could not close the stream");
			}			
		}
		LOG.debug("Finish passing stream " + fileName + " to delivery method");
	}

	/**
	 * Does nothing because all streams have been already passed through delivery method and closed.
	 */
	public void finish() {
		LOG.debug("Null packager is notified that all entries were added");
	}
}
