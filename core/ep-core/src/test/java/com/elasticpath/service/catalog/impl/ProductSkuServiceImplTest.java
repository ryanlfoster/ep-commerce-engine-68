/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuOrderingField;
import com.elasticpath.service.changeset.ChangeSetMemberSortingField;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>AttributeServiceImpl</code>.
 */
public class ProductSkuServiceImplTest extends AbstractEPServiceTestCase {

	private static final long PRODUCT_SKU_UID_EXISTS = 1L;

	private static final String SKU_CODE = "SKU_CODE";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private ProductSkuServiceImpl productSkuService;
	private ProductService mockProductService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		productSkuService = new ProductSkuServiceImpl();

		productSkuService.setPersistenceEngine(getPersistenceEngine());
//		productSkuService.setFetchPlanHelper(getMockFetchPlanHelper());
		mockProductService = context.mock(ProductService.class);
		productSkuService.setProductService(mockProductService);
	}

	/**
	 * Test method for 'ProductTypeServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		productSkuService.setPersistenceEngine(null);
		try {
			productSkuService.saveOrUpdate(new ProductSkuImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(productSkuService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.add(Attribute)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		stubGetBean(ContextIdNames.PRODUCT_SERVICE, mockProductService);

		Product product = new ProductImpl();
		product.initialize();
		final ProductSku productSku = new ProductSkuImpl();
		productSku.setProduct(product);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).saveOrMerge(with(same(productSku)));
				will(returnValue(productSku));

				oneOf(mockProductService).notifySkuUpdated(with(same(productSku)));
			}
		});


		ProductSkuServiceImpl productSkuService = new ProductSkuServiceImpl() {
			@Override
			protected void throwExceptionIfDuplicate(final ProductSku sku) { //NOPMD
				//Overide this mothod for some odd Class cast error in this mothod
			}
		};
		productSkuService.setPersistenceEngine(getPersistenceEngine());


		productSkuService.saveOrUpdate(productSku);
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.removeProductSkuTree(long)'.
	 */
	@Test
	public void testRemoveProductSkuTree() {
		final long productSkuUid = 23456L;
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		productSku.setUidPk(productSkuUid);
		ArrayList<Long> productUidList = new ArrayList<Long>();
		productUidList.add(productSku.getUidPk());

		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).executeNamedQuery("PRODUCTCATEGORY_DELETE_BY_CATEGORY_UID", productSku.getUidPk());

				allowing(getMockPersistenceEngine()).bulkUpdate(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(1));

				allowing(getMockPersistenceEngine()).delete(with(same(productSku)));
			}
		});

	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.findUidBySkuCode(skuCode)'.
	 */
	@Test
	public void testFindUidBySkuCodeNoResults() {

		// test that an empty result returns zero ID
		context.checking(new Expectations() {
			{
				List<Long> resultEmpty = new ArrayList<Long>();

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("PRODUCT_SKU_UID_SELECT_BY_GUID", SKU_CODE);
				will(returnValue(resultEmpty));
			}
		});

		long skuUid = productSkuService.findUidBySkuCode(SKU_CODE);
		assertEquals("Returned UID_PK must be zero.", 0, skuUid);
	}

	@Test
	public void testFindUidBySkuCodeSingleResult() {
		// test that single entry result returns the value found
		context.checking(new Expectations() {
			{
				List<Long> resultSingle = new ArrayList<Long>();
				resultSingle.add(PRODUCT_SKU_UID_EXISTS);

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("PRODUCT_SKU_UID_SELECT_BY_GUID", SKU_CODE);
				will(returnValue(resultSingle));
			}
		});

		long skuUid = productSkuService.findUidBySkuCode(SKU_CODE);
		assertEquals("Returned UID_PK must be found.", PRODUCT_SKU_UID_EXISTS, skuUid);

	}

	@Test(expected = EpSystemException.class)
	public void testFindUidBySkuCodeMultipleResults() {
		// test that duplicate matches throw an EpSystemException
		context.checking(new Expectations() {
			{
				List<Long> resultMultiple = new ArrayList<Long>();
				resultMultiple.add(PRODUCT_SKU_UID_EXISTS);
				resultMultiple.add(PRODUCT_SKU_UID_EXISTS + 1);

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("PRODUCT_SKU_UID_SELECT_BY_GUID", SKU_CODE);
				will(returnValue(resultMultiple));
			}
		});

		productSkuService.findUidBySkuCode(SKU_CODE);
	}

	/**
	 * Tests that when GroupId is null we get the expected exception with the message formatted correctly.
	 */
	@Test
	public void testfindSkusByProductCodeNull1OrderingField() {
		ProductSkuServiceImpl service = new ProductSkuServiceImpl();

		boolean expectedExceptionOccurred = false;
		try {
			DirectedSortingField [] orderingField = new DirectedSortingField [] {
					new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING) };
			service.findSkusByProductCode(null, 0, 0, orderingField, null);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value argument: productCode=null, sortingField=OBJECTIDENTIFIER, ASC",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}

	/**
	 * Tests that when GroupId is null we get the expected exception with the message formatted correctly.
	 */
	@Test
	public void testFindGroupMembersByGroupIdNull2OrderingFields() {
		ProductSkuServiceImpl service = new ProductSkuServiceImpl();

		boolean expectedExceptionOccurred = false;
		try {
			DirectedSortingField [] twoOrderingFields = new DirectedSortingField [] {
					new DirectedSortingField(ChangeSetMemberSortingField.OBJECT_ID, SortingDirection.ASCENDING),
					new DirectedSortingField(ProductSkuOrderingField.SKU_CODE, SortingDirection.DESCENDING) };
			service.findSkusByProductCode(null, 0, 0, twoOrderingFields, null);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value argument: productCode=null, sortingField=OBJECTIDENTIFIER, ASC,"
					+ "SKUCODEINTERNAL, DESC",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}

	/**
	 * Tests that when ordering field is null we get the expected exception with the message formatted correctly.
	 */
	@Test
	public void testFindGroupMembersByOrderingFieldNull() {
		ProductSkuServiceImpl service = new ProductSkuServiceImpl();

		boolean expectedExceptionOccurred = false;
		try {
			service.findSkusByProductCode("prod", 0, 0, null, null);
		} catch (IllegalArgumentException e) {
			assertEquals(
					"Expect the message in this format",
					"Null-value argument: productCode=prod, sortingField=null",
					e.getMessage());
			expectedExceptionOccurred = true;
		}
		assertTrue("Expect an IllegalArgumentException", expectedExceptionOccurred);
	}
}
