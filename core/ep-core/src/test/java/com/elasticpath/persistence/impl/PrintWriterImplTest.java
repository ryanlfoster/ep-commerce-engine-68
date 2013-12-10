package com.elasticpath.persistence.impl;

import java.io.File;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>PrintWriterImpl</code>.
 */
public class PrintWriterImplTest {


	private PrintWriterImpl printWriterImpl;

	@Before
	public void setUp() throws Exception {
		this.printWriterImpl = new PrintWriterImpl();
	}

	/**
	 * Test method for 'com.elasticpath.persistence.impl.printWriterImpl.open(String)'.
	 */
	@Test
	public void testOpen() {
		final String fileName = System.getProperty("java.io.tmpdir") + File.separator + "testPrintWriter" + UUID.randomUUID() + ".txt";
		printWriterImpl.open(fileName);
		printWriterImpl.println("Some strings.");
		printWriterImpl.close();

		File deleteMe = new File(fileName);
		deleteMe.delete();
	}
}
