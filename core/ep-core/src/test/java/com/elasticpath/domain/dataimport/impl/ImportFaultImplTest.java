package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportFault;

/**
 * Test <code>ImportFaultImpl</code>.
 */
public class ImportFaultImplTest {


	private ImportFaultImpl faultWarning;

	private ImportFaultImpl faultError;

	@Before
	public void setUp() throws Exception {
		this.faultWarning = new ImportFaultImpl();
		this.faultWarning.setLevel(ImportFault.WARNING);

		this.faultError = new ImportFaultImpl();
		this.faultError.setLevel(ImportFault.ERROR);
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.getLevel()'.
	 */
	@Test
	public void testGetLevel() {
		assertEquals(ImportFault.WARNING, faultWarning.getLevel());
		assertEquals(ImportFault.ERROR, faultError.getLevel());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.setLevel(int)'.
	 */
	@Test
	public void testSetLevel() {
		// do nothing
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.isWarning()'.
	 */
	@Test
	public void testIsWarning() {
		assertTrue(faultWarning.isWarning());
		assertFalse(faultError.isWarning());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.isError()'.
	 */
	@Test
	public void testIsError() {
		assertTrue(faultError.isError());
		assertFalse(faultWarning.isError());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.getCode()'.
	 */
	@Test
	public void testGetCode() {
		assertNull(faultError.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.setCode(String)'.
	 */
	@Test
	public void testSetCode() {
		final String code = "dummyCode";
		faultError.setCode(code);
		assertSame(code, faultError.getCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.getSource()'.
	 */
	@Test
	public void testGetSource() {
		assertNull(faultError.getSource());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.setSource(String)'.
	 */
	@Test
	public void testSetSource() {
		final Exception source = new Exception(); // NOPMD
		faultError.setSource(source.getLocalizedMessage());
		assertSame(source.getLocalizedMessage(), faultError.getSource());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.getArgs()'.
	 */
	@Test
	public void testGetArgs() {
		assertNotNull(faultError.getArgs());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportFaultImpl.setArgs(Object[])'.
	 */
	@Test
	public void testSetArgs() {
		final Object[] args = new String[] { "aaa", "bbb" };
		faultError.setArgs(args);
		final Object[] returnedArgs = faultError.getArgs();
		assertEquals("aaa", returnedArgs[0]);
		assertEquals("bbb", returnedArgs[1]);
	}
}
