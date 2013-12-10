package com.elasticpath.service.catalog.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test {@link BrandServiceImpl}.
 */
@SuppressWarnings({"PMD.TooManyStaticImports" })
public class BrandServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final BrandServiceImpl brandServiceImpl = new BrandServiceImpl();
	private PersistenceEngine persistenceEngine;
	private ProductService productService;
	private CatalogService catalogService;
	private BeanFactory beanFactory;
	private Brand brand;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		brand = context.mock(Brand.class);
		persistenceEngine = context.mock(PersistenceEngine.class);
		productService = context.mock(ProductService.class);
		catalogService = context.mock(CatalogService.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		brandServiceImpl.setPersistenceEngine(persistenceEngine);
		brandServiceImpl.setProductService(productService);
		brandServiceImpl.setCatalogService(catalogService);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.add(Brand)'.
	 */
	@Test
	public void testAdd() {
		context.checking(new Expectations() {
			{
				allowing(brand).getCode();
				will(returnValue(null));

				oneOf(persistenceEngine).save(brand);
			}
		});

		brandServiceImpl.add(brand);
	}

	/** Default implementation for {@link BrandServiceImplTest}. */
	private abstract class AbstractTestBrandImpl implements Brand {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.get(long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBeanImplClass(ContextIdNames.BRAND);
				will(returnValue(AbstractTestBrandImpl.class));
				allowing(persistenceEngine).get(AbstractTestBrandImpl.class, uid);
				will(returnValue(brand));
			}
		});
		assertSame(brand, brandServiceImpl.get(uid));
		assertSame(brand, brandServiceImpl.getObject(uid));

		final long nonExistUid = 3456L;

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).get(AbstractTestBrandImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});
		assertNull(brandServiceImpl.get(nonExistUid));

		context.checking(new Expectations() {
			{
				Brand brand = context.mock(Brand.class, "newBrand");
				allowing(brand).getUidPk();
				will(returnValue(0L));

				allowing(beanFactory).getBean(ContextIdNames.BRAND);
				will(returnValue(brand));
			}
		});
		assertEquals(0, brandServiceImpl.get(0).getUidPk());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.saveOrUpdate(Brand)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final Brand updatedBrand = context.mock(Brand.class, "updatedBrand");

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).saveOrUpdate(brand);
				will(returnValue(updatedBrand));
				oneOf(productService).notifyBrandUpdated(updatedBrand);
			}
		});

		brandServiceImpl.saveOrUpdate(brand);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.remove(Brand)'.
	 */
	@Test
	public void testRemove() {
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).delete(brand);
			}
		});

		brandServiceImpl.remove(brand);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.getBrandInUseList()'.
	 */
	@Test
	public void testGetBrandInUseList() {
		final List<Brand> brands = new ArrayList<Brand>();

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(with(containsString("BRANDS_IN_USE")),
						with(any(Object[].class)));
				will(returnValue(brands));
			}
		});

		assertSame(brands, brandServiceImpl.getBrandInUseList());
	}
	
	/**
	 * Test method for {@link BrandServiceImpl#findAllBrandsFromCatalog(long)}.
	 */
	@Test
	public void testFindAllBrandsFromCatalog() {
		final List<Brand> brandList = Collections.singletonList(brand);
		final long catalogUid = 1234L;

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("BRAND_SELECT_CATALOG_ALL", catalogUid);
				will(returnValue(brandList));
			}
		});

		assertSame(brandList, brandServiceImpl.findAllBrandsFromCatalog(catalogUid));
	}

	/**
	 * Test method for {@link BrandServiceImpl#findAllBrandsFromCatalogList(Collection/<Long/>)}.
	 */
	@Test
	public void testFindAllBrandsFromCatalogList() {
		final Catalog catalog = context.mock(Catalog.class);
		final long catalogUid = 10001L;
		context.checking(new Expectations() {
			{
				allowing(catalog).getUidPk();
				will(returnValue(catalogUid));
				allowing(catalog);
			}
		});


		final List<Brand> brandList = Collections.singletonList(brand);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQueryWithList("BRAND_SELECT_CATALOG_IN_LIST", "list",
						Collections.singletonList(catalogUid));
				will(returnValue(brandList));
				oneOf(catalogService).findMastersUsedByVirtualCatalog(with(any(String.class)));
				will(returnValue(Collections.singletonList(catalog)));
			}
		});

		assertSame(brandList, brandServiceImpl.findAllBrandsFromCatalogList(Collections.singleton(catalog)));
	}
	
	/**
	 * Test method for {@link BrandServiceImpl#isInUse(long)}.
	 */
	@Test
	public void testIsInUse() {
		final long nonExistantBrandUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(equalTo("BRAND_IN_USE")), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertFalse(brandServiceImpl.isInUse(nonExistantBrandUid));

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(equalTo("BRAND_IN_USE")), with(any(Object[].class)));
				will(returnValue(Collections.singletonList(1L)));
			}
		});
		assertTrue(brandServiceImpl.isInUse(nonExistantBrandUid));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.BrandServiceImpl.update(Brand)'.
	 */
	@Test
	public void testUpdate() {
		final Brand updatedBrand = context.mock(Brand.class, "updatedBrand");

		context.checking(new Expectations() {
			{
				allowing(brand).getCode();
				will(returnValue(null));

				oneOf(persistenceEngine).merge(brand);
				will(returnValue(updatedBrand));
				oneOf(productService).notifyBrandUpdated(updatedBrand);
			}
		});

		brandServiceImpl.update(brand);
	}
}
