/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Test case for {@link AbstractCatalogImportFieldImpl}.
 */
public class AbstractCatalogImportFieldImplTest {
	
	private AbstractCatalogImportFieldImpl importField;

	@Before
	public void setUp() throws Exception {
		importField = new AbstractCatalogImportFieldImpl(null, null, false, false) {
			private static final long serialVersionUID = 3726607602485928511L;

			public String getStringValue(final Object persistenceObject) {
				// stub
				return null;
			}

			public void setStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
				// stub
			}
		};
	}
	
	/**
	 * Test method for {@link AbstractCatalogImportFieldImpl#getCatalog()}.
	 */
	@Test
	public void testGetCatalog() {
		try {
			importField.getCatalog();
			fail("EpDomainException expected");
		} catch (EpDomainException e) {
			assertNotNull(e);
		}
		
		final Catalog catalog = new CatalogImpl();
		importField.setCatalog(catalog);
		assertSame(catalog, importField.getCatalog());
		
		importField.setCatalog(null);
		try {
			importField.getCatalog();
			fail("EpDomainException expected");
		} catch (EpDomainException e) {
			assertNotNull(e);
		}
	}
}
