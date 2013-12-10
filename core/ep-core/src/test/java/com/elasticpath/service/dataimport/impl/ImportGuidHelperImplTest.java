package com.elasticpath.service.dataimport.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 *
 */
public class ImportGuidHelperImplTest extends AbstractEPServiceTestCase {

	private static final String CATALOG_CODE = "catalogCode";

	private static final String EP_SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String NON_EXIST_GUID = "NON_EXIST_GUID";

	private static final String SOME_GUID = "SOME_GUID";

	private CategoryService mockCategoryService;

	private CategoryService categoryService;

	private ImportGuidHelperImpl importGuidHelper;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setupCategoryService();

		final PersistenceSession mockPersistenceSession = context.mock(PersistenceSession.class);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).getSharedPersistenceSession();
				will(returnValue(mockPersistenceSession));
			}
		});

		this.importGuidHelper = new ImportGuidHelperImpl();
		this.importGuidHelper.setCategoryService(categoryService);
		this.importGuidHelper.setPersistenceEngine(getPersistenceEngine());
	}


	private void setupCategoryService() {
		// Mock CategoryService
		mockCategoryService = context.mock(CategoryService.class);
		this.categoryService = mockCategoryService;
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductByGuid(String)'.
	 */
	@Test
	public void testFindProductByGuidWithNullReturn() {
		stubGetBean(ContextIdNames.PRODUCT, ProductImpl.class);
		context.checking(new Expectations() {
			{
		
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(this.importGuidHelper.findProductByGuid(NON_EXIST_GUID, true, true, true));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductByGuid(String)'.
	 */
	@Test
	public void testFindProductByGuidWithOneReturn() {
		stubGetBean(ContextIdNames.PRODUCT, ProductImpl.class);

		final List<Product> products = new ArrayList<Product>();
		Product product = new ProductImpl();
		products.add(product);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(products));
			}
		});
		assertSame(product, this.importGuidHelper.findProductByGuid(SOME_GUID, true, true, true));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductByGuid(String)'.
	 */
	@Test
	public void testFindProductByGuidWithMoreThanOneReturn() {
		stubGetBean(ContextIdNames.PRODUCT, ProductImpl.class);

		final List<Product> products = new ArrayList<Product>();
		Product product = new ProductImpl();
		products.add(product);
		products.add(product);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(products));
			}
		});
		try {
			this.importGuidHelper.findProductByGuid(SOME_GUID, false, false, true);
			fail(EP_SERVICE_EXCEPTION_EXPECTED);
		} catch (EpServiceException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findCustomerByGuid(String)'.
	 */
	@Test
	public void testFindCustomerByGuidWithOneReturn() {
		final List<Customer> customers = new ArrayList<Customer>();
		Customer customer = new CustomerImpl();
		customers.add(customer);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(customers));
			}
		});
		assertSame(customer, importGuidHelper.findCustomerByGuid(SOME_GUID));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findCustomerByGuid(String)'.
	 */
	@Test
	public void testFindCustomerByGuidWithMoreThanOneReturn() {
		final List<Customer> customers = new ArrayList<Customer>();
		Customer customer = new CustomerImpl();
		customers.add(customer);
		customers.add(customer);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(customers));
			}
		});
		try {
			importGuidHelper.findCustomerByGuid(SOME_GUID);
			fail(EP_SERVICE_EXCEPTION_EXPECTED);
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findCustomerByGuid(String)'.
	 */
	@Test
	public void testFindCustomerByGuidWithNullReturn() {
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(this.importGuidHelper.findCustomerByGuid(NON_EXIST_GUID));
	};

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductSkuByGuid(String)'.
	 */
	@Test
	public void testFindProductSkuByGuidWithNullReturn() {
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(this.importGuidHelper.findProductSkuByGuid(NON_EXIST_GUID));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductSkuByGuid(String)'.
	 */
	@Test
	public void testFindProductSkuByGuidWithOneReturn() {
		final List<ProductSku> productSkus = new ArrayList<ProductSku>();
		ProductSku productSku = new ProductSkuImpl();
		productSkus.add(productSku);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(productSkus));
			}
		});
		assertSame(productSku, this.importGuidHelper.findProductSkuByGuid(SOME_GUID));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findProductSkuByGuid(String)'.
	 */
	@Test
	public void testFindProductSkuByGuidWithMoreThanOneReturn() {
		final List<ProductSku> productSkus = new ArrayList<ProductSku>();
		ProductSku productSku = new ProductSkuImpl();
		productSkus.add(productSku);
		productSkus.add(productSku);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(productSkus));
			}
		});
		try {
			this.importGuidHelper.findProductSkuByGuid(SOME_GUID);
			fail(EP_SERVICE_EXCEPTION_EXPECTED);
		} catch (EpServiceException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ImportGuidHelperImpl.getCategoryService()'.
	 */
	@Test
	public void testGetCategoryService() {
		assertSame(categoryService, this.importGuidHelper.getCategoryService());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findBrandByGuid(String)'.
	 */
	@Test
	public void testFindBrandByGuidWithNullReturn() {
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(Collections.emptyList()));
			}
		});
		assertNull(this.importGuidHelper.findBrandByGuidAndCatalogGuid(NON_EXIST_GUID, CATALOG_CODE));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findBrandByGuid(String)'.
	 */
	@Test
	public void testFindBrandByGuidWithOneReturn() {
		final List<Brand> brands = new ArrayList<Brand>();
		Brand brand = new BrandImpl();
		brands.add(brand);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(brands));
			}
		});
		assertSame(brand, this.importGuidHelper.findBrandByGuidAndCatalogGuid(SOME_GUID, CATALOG_CODE));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.AbstractImportJobRunnerImpl.findBrandByGuid(String)'.
	 */
	@Test
	public void testFindBrandByGuidWithMoreThanOneReturn() {
		final List<Brand> brands = new ArrayList<Brand>();
		Brand brand = new BrandImpl();
		brands.add(brand);
		brands.add(brand);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(brands));
			}
		});
		try {
			this.importGuidHelper.findBrandByGuidAndCatalogGuid(SOME_GUID, CATALOG_CODE);
			fail(EP_SERVICE_EXCEPTION_EXPECTED);
		} catch (EpServiceException e) {
			// succeed!
			assertNotNull(e);
		}
	}
}
