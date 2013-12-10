package com.elasticpath.importexport.common.util.runner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.EngineRuntimeException;

/**
 * AbstractPipedStreamRunner implements processing of piped streams in separate thread.
 */
public abstract class AbstractPipedStreamRunner implements Runnable, PipedStreamRunner {
	
	private static final Logger LOG = Logger.getLogger(AbstractPipedStreamRunner.class);
	
	/*
	 * Output stream is used by some job processing chain link to fill data into.
	 */
	private PipedOutputStream pipeOut;
	
	/*
	 * Input stream is used by the next job processing chain link to read data written by previous chain link. 
	 */
	private PipedInputStream pipeIn;

	/**
	 * Constructor creates connected piped streams.
	 */
	public AbstractPipedStreamRunner() {
		try {
			pipeOut = new PipedOutputStream();
			pipeIn = new PipedInputStream(pipeOut);
		} catch (IOException e) {
			throw new EngineRuntimeException("IE-40300", e);
		}
	}

	@Override
	public InputStream createResultStream() {
		new Thread(this).start();
		return pipeIn;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Executes runInternal method which must be implemented by clients.
	 */
	public final void run() {
		try {
			runInternal(pipeOut);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				pipeOut.close();
			} catch (IOException e) {
				LOG.error("Could not close PipedStream", e);
			}			
		}				
	}

	/**
	 * Client classes must implement this method and write processed data into the given output stream.
	 * 
	 * @param outputStream output stream to write processed data into
	 */
	protected abstract void runInternal(final OutputStream outputStream);
}
