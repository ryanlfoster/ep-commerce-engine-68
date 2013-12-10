package com.elasticpath.importexport.importer.controller.impl;

import org.junit.Test;

import com.elasticpath.importexport.common.exception.ConfigurationException;

/**
 * Test for import controller implementation.
 */
public class ImportControllerImplTest {

	private final ImportControllerImpl importControllerImpl = new ImportControllerImpl();

	/**
	 * Check import processing without initialized context.
	 */
	@Test(expected = ConfigurationException.class)
	public void testExecuteImportWithoutContext() throws Exception {
		importControllerImpl.executeImport();
	}

}
