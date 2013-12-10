package com.elasticpath.importexport.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.Test;

import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.util.runner.AbstractPipedStreamRunner;

/**
 * Tests AbstractPipedStreamRunner.
 */
public class AbstractPipedStreamRunnerTest {

	/**
	 * Tests GetResultStream.
	 *
	 * @throws IOException for probable errors while reading.
	 */
	@Test
	public void testGetResultStream() throws IOException {
		final String content = "Some Content";
		
		final InputStream stream = new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				try {
					outputStream.write(content.getBytes());
				} catch (IOException e) {
					fail("Exception must not be thrown");
				}
				
			}
		} .createResultStream();
		
		final BufferedReader lineStream = new BufferedReader(new InputStreamReader(stream));
		assertEquals(content, lineStream.readLine());
		assertSame(lineStream.read(), -1);
		lineStream.close();
	}
	
	/**
	 * Tests the get result stream with operation that throws exception. 
	 * @throws IOException for probable errors while reading.
	 */
	@Test
	public void testGetResultStreamWithException() throws IOException {
		final InputStream stream = new AbstractPipedStreamRunner() {
			@Override
			protected void runInternal(final OutputStream outputStream) {
				throw new ImportRuntimeException("Intentional exception");
				
			}
		} .createResultStream();
		
		assertEquals(-1, stream.read());
	}
}
