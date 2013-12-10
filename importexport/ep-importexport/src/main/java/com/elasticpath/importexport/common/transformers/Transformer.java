package com.elasticpath.importexport.common.transformers;

import java.io.InputStream;

/**
 * This interface provides method for transformation <code>InputStream</code> to another <code>InputStream</code> based on some rules.
 */
public interface Transformer {

	/**
	 * Transforms input stream into another input stream.
	 * 
	 * @param inputStream the imputStream
	 * @return transformed inputStream
	 */
	InputStream transform(final InputStream inputStream);
}
