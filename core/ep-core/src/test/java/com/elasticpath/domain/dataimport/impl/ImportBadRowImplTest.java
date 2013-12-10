package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.dataimport.ImportFault;

/**
 * Test <code>ImportBadRowImpl</code>.
 */
public class ImportBadRowImplTest {

	private ImportBadRowImpl importBadRow;

	@Before
	public void setUp() throws Exception {
		this.importBadRow = new ImportBadRowImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.getRowNumber()'.
	 */
	@Test
	public void testGetRowNumber() {
		assertEquals(0, this.importBadRow.getRowNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.setRowNumber(int)'.
	 */
	@Test
	public void testSetRowNumber() {
		this.importBadRow.setRowNumber(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.importBadRow.getRowNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.getRow()'.
	 */
	@Test
	public void testGetRow() {
		assertNull(this.importBadRow.getRow());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.setRow(String)'.
	 */
	@Test
	public void testSetRow() {
		final String row = "aaa,bbb";
		this.importBadRow.setRow(row);
		assertSame(row, this.importBadRow.getRow());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.getErrorMsgs()'.
	 */
	@Test
	public void testGetErrorMsgs() {
		assertTrue(this.importBadRow.getImportFaults().isEmpty());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.setErrorMsgs(List)'.
	 */
	@Test
	public void testSetErrorMsg() {
		final List<ImportFault> errMsgs = new ArrayList<ImportFault>();
		this.importBadRow.setImportFaults(errMsgs);
		assertSame(errMsgs, this.importBadRow.getImportFaults());
	}

	/**
	 * Test method for 'com.elasticpath.domain.dataimport.impl.ImportBadRowImpl.addErrorMsgs(String)'.
	 */
	@Test
	public void testAddImportFault() {
		final ImportFault importFault = new ImportFaultImpl();
		this.importBadRow.addImportFault(importFault);
		assertTrue(this.importBadRow.getImportFaults().contains(importFault));
	}
}
