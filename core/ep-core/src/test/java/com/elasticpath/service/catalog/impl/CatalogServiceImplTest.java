/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test cases for <code>CatalogServiceImpl</code>.
 */
public class CatalogServiceImplTest extends AbstractEPServiceTestCase {

	private CatalogService catalogServiceImpl;


	/**
	 * Prepares for tests.
	 *
	 * @throws java.lang.Exception in case of any errors
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		catalogServiceImpl = new CatalogServiceImpl();
		catalogServiceImpl.setPersistenceEngine(getPersistenceEngine());
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#saveOrUpdate(Catalog)}.
	 */
	@Test
	public final void testSaveOrUpdate() {
		final Catalog catalog = new CatalogImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(catalog)));
				will(returnValue(catalog));
			}
		});
		catalogServiceImpl.saveOrUpdate(catalog);
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#getCatalog(long)}.
	 */
	@Test
	public final void testGetCatalog() {
		final long uid = 1234L;
		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(uid);
		stubGetBean(ContextIdNames.CATALOG, CatalogImpl.class);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(CatalogImpl.class, uid);
				will(returnValue(catalog));
			}
		});
		assertSame(catalog, catalogServiceImpl.getCatalog(uid));
		assertSame(catalog, catalogServiceImpl.getObject(uid));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(CatalogImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});
		assertNull(catalogServiceImpl.getCatalog(nonExistUid));
		stubGetBean(ContextIdNames.CATALOG, CatalogImpl.class);
		assertEquals(0, catalogServiceImpl.getCatalog(0).getUidPk());
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#findAllCatalogUids()}.
	 */
	@Test
	public final void testFindAllCatalogUids() {
		final List<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_CATALOG_UIDS"), with(any(Object[].class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, catalogServiceImpl.findAllCatalogUids());

		final long catalogUid = 1234L;
		uidList.add(catalogUid);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_CATALOG_UIDS"), with(any(Object[].class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, catalogServiceImpl.findAllCatalogUids());
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#findAllCatalogs()}.
	 */
	@Test
	public final void testFindAllCatalogs() {
		final List<Catalog> catalogList = new ArrayList<Catalog>();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_ALL_CATALOGS"), with(any(Object[].class)));
				will(returnValue(catalogList));
			}
		});
		assertSame(catalogList, catalogServiceImpl.findAllCatalogs());
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#findByName()}.
	 */
	@Test
	public final void testFindByName() {
		final List<Catalog> catalogList = new ArrayList<Catalog>();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_BY_NAME"), with(any(Object[].class)));
				will(returnValue(catalogList));
			}
		});
		assertSame(null, catalogServiceImpl.findByName("sampleCatalog"));

		final long uid = 1234L;
		final Catalog catalog = new CatalogImpl();
		catalog.setUidPk(uid);
		catalog.setName("sampleCatalog");
		catalogList.add(catalog);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_BY_NAME"), with(any(Object[].class)));
				will(returnValue(catalogList));
			}
		});
		assertSame(catalog, catalogServiceImpl.findByName("sampleCatalog"));
	}

	/**
	 * Test method for {@link com.elasticpath.service.catalog.impl.CatalogServiceImpl#nameExists()}.
	 */
	@Test
	public final void testNameExists() {
		final String catalogName = "test catalog";
		final String existCatalogName = catalogName;
		final Object[] parameters = new Object[] { existCatalogName };
		final Catalog sampleCatalog = new CatalogImpl();
		final long uidPk1 = 1L;
		sampleCatalog.setUidPk(uidPk1);
		sampleCatalog.setName(catalogName);
		final List<Catalog> catalogList = new ArrayList<Catalog>();
		catalogList.add(sampleCatalog);

		// Test emailExists(String)email
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("FIND_CATALOG_BY_NAME", parameters);
				will(returnValue(catalogList));
			}
		});
		assertTrue(catalogServiceImpl.nameExists(catalogName));
	}

	/**
	 * Test method for {@link CatalogServiceImpl#codeExists(String)}.
	 */
	@Test
	public void testCodeExists() {
		final String nonExistingCode = "some code";
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_UID_BY_CODE"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Long>()));
			}
		});
		assertFalse(catalogServiceImpl.codeExists(nonExistingCode));

		final String existingCode = "another code";
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("FIND_CATALOG_UID_BY_CODE"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(1L)));
			}
		});
		assertTrue(catalogServiceImpl.codeExists(existingCode));
	}

	/**
	 * Test method for {@link CatalogServiceImpl#catalogInUse(long)}.
	 */
	@Test
	public void testCatalogInUse() {
		final long uid = 1234L;
		final ArrayList<Long> users = new ArrayList<Long>();
		users.add(uid);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATALOG_IN_USE_BY_CMUSER"), with(any(Object[].class)));
				will(returnValue(users));

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));
			}
		});
		assertTrue("Testing true condition", catalogServiceImpl.catalogInUse(uid));
	}

	@Test
	public void testCatalogNotInUse() {
		final long uid = 1234L;
		final ArrayList<Long> users = new ArrayList<Long>();
		users.add(uid);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATALOG_IN_USE_BY_CMUSER"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Long>()));

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));
			}
		});
		assertFalse("Testing false condition", catalogServiceImpl.catalogInUse(uid));
	}
}
