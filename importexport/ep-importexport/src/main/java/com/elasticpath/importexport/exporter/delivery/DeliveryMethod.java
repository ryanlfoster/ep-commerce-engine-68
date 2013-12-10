package com.elasticpath.importexport.exporter.delivery;

import java.io.OutputStream;

/**
 * Deliver streams to destination: file system, FTP server etc.  
 */
public interface DeliveryMethod {
	
	/**
	 * Starts next file's delivering process.
	 *
	 * @param fileName the name of file to be delivered
	 * @return outputStream of destination to deliver into
	 */
	OutputStream deliver(final String fileName);
}
