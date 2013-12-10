package com.elasticpath.importexport.common.transformers.impl;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;

/**
 * The superclass for all implementations of transformers.
 * <p>
 * It is responsible for wrapping streams into pipes and run new thread for transformation process.
 */
public abstract class AbstractTransformerImpl implements Transformer {

	private static final Logger LOG = Logger.getLogger(AbstractTransformerImpl.class);

	/**
	 * Perform actual transformation.
	 * 
	 * @param input stream with marshalled object to be transformed
	 * @param output stream to place transformed input stream into
	 */
	protected abstract void processTransformation(final InputStream input, final OutputStream output);

	/**
	 * Wrap streams with pipes and run transformation in new thread.
	 * 
	 * @param inputStream the stream to be transformed
	 * @return inputStream piped with output stream produced after transformation
	 */
	public final InputStream transform(final InputStream inputStream) {
		return new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				LOG.debug("Transformation process initiated");
				processTransformation(inputStream, outputStream);
			}
		} .createResultStream();
	}
}
