package com.elasticpath.importexport.common.util.runner;

import java.io.InputStream;

/**
 * Piped Stream Runner is used to access the input stream connected with processed output stream by pipe.
 */
public interface PipedStreamRunner {

	/**
	 * Gets result InputStream.
	 *
	 * @return InputStream containing processed data
	 */
	InputStream createResultStream();
}
