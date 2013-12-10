/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>ProductAssociationServiceImpl</code>.
 *
 */
public class ProductAssociationServiceImplTest {

	private ProductAssociationServiceImpl productAssociationService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private FetchPlanHelper mockFetchPlanHelper;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		productAssociationService = new ProductAssociationServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		productAssociationService.setPersistenceEngine(mockPersistenceEngine);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(new ProductAssociationImpl()));
			}
		});
		mockFetchPlanHelper = context.mock(FetchPlanHelper.class);
		productAssociationService.setFetchPlanHelper(mockFetchPlanHelper);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test behaviour when the persistence engine is not set.
	 */
	@Test(expected = EpServiceException.class)
	public void testPersistenceEngineIsNull() {
		productAssociationService.setPersistenceEngine(null);
		productAssociationService.add(new ProductAssociationImpl());
	}

	/**
	 * Test method for 'com.elasticpath.service.productAssociationServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(productAssociationService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.productAssociationServiceImpl.add(RuleSet)'.
	 */
	@Test
	public void testAdd() {
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(productAssociation)));
			}
		});
		productAssociationService.add(productAssociation);
	}

	/**
	 * Test method for 'com.elasticpath.service.productAssociationServiceImpl.add(RuleSet)'.
	 */
	@Test
	public void testUpdate() {
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).update(with(same(productAssociation)));
				will(returnValue(productAssociation));
			}
		});
		productAssociationService.update(productAssociation);
	}

	/**
	 * Test method for 'com.elasticpath.service.productAssociationServiceImpl.delete(RuleSet)'.
	 */
	@Test
	public void testRemove() {
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(productAssociation)));
			}
		});
		productAssociationService.remove(productAssociation);
	}

	/**
	 * Test method for 'com.elasticpath.service.productAssociationServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(ProductAssociationImpl.class, uid);
				will(returnValue(productAssociation));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(ProductAssociationImpl.class));
			}
		});
		final ProductAssociation loadedProductAssociation = productAssociationService.load(uid);
		assertSame(productAssociation, loadedProductAssociation);
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductAssociationServiceImpl.get(Long)'.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(ProductAssociationImpl.class, uid);
				will(returnValue(productAssociation));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(ProductAssociationImpl.class));
			}
		});
		final ProductAssociation loadedProductAssociation = (ProductAssociation) productAssociationService.getObject(uid);
		assertSame(productAssociation, loadedProductAssociation);
	}

	/**
	 * Test that the <code>getTuned</code> method configures the fetch plan helper.
	 */
	@Test
	public void testGetTuned() {
		final long uid = 1234L;
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setUidPk(uid);

		ProductAssociationLoadTuner mockLoadTuner = context.mock(ProductAssociationLoadTuner.class);

		context.checking(new Expectations() {
			{
				oneOf(mockFetchPlanHelper).configureProductAssociationFetchPlan(with(any(ProductAssociationLoadTuner.class)));
				oneOf(mockFetchPlanHelper).clearFetchPlan();

				allowing(mockPersistenceEngine).load(ProductAssociationImpl.class, uid);
				will(returnValue(productAssociation));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.PRODUCT_ASSOCIATION);
				will(returnValue(ProductAssociationImpl.class));
			}
		});

		final ProductAssociation loadedAssociation = productAssociationService.getTuned(uid, mockLoadTuner);
		assertSame(productAssociation, loadedAssociation);
	}

	/**
	 * Tests duplicate elimination of product associations.
	 * When 2 products A and B are both associated to the same product C
	 * then the product association to C should only be returned once.
	 */
	@SuppressWarnings("serial")
	@Test
	public void testDuplicateElimination() {

		// Create 2 mock ProductAssociations.
		final ProductAssociation assocA = context.mock(ProductAssociation.class, "assocA");
		final ProductAssociation assocB = context.mock(ProductAssociation.class, "assocB");
		context.checking(new Expectations() {
			{
				allowing(assocA).isValidProductAssociation(); will(returnValue(true));
				allowing(assocB).isValidProductAssociation(); will(returnValue(true));
			}
		});

		// Create 2 stubbed StoreProducts.
		// NOTE We want real Impls here instead of mocks so that we can
		// exercise implementation methods like equals/hashCode etc.
		// Use the same code for both products to test duplicate elimination.
		// Also, storeProduct.getAssociationsByType(...) should return the
		// right associations so that it's all plumbed together.
		final HashSet<ProductAssociation> assocSetA = new HashSet<ProductAssociation>();
		assocSetA.add(assocA);
		final HashSet<ProductAssociation> assocSetB = new HashSet<ProductAssociation>();
		assocSetB.add(assocB);
		final StoreProduct productA = new StoreProductImpl(null) {
			@Override
			public String getCode() { return "TEST CODE"; }
			@Override
			public Set<ProductAssociation> getAssociationsByType(final int associationType, final Set<Product> filterTargetProducts) {
				return assocSetA;
			}
		};
		final StoreProduct productB = new StoreProductImpl(null) {
			@Override
			public String getCode() { return "TEST CODE"; }
			@Override
			public Set<ProductAssociation> getAssociationsByType(final int associationType, final Set<Product> filterTargetProducts) {
				return assocSetB;
			}
		};

		// ProductAssociation.getTargetProduct has to return A and B.
		context.checking(new Expectations() {
			{
				allowing(assocA).getTargetProduct(); will(returnValue(productA));
				allowing(assocB).getTargetProduct(); will(returnValue(productB));
			}
		});

		// Stub out ProductAssociationService.computeTopProductAssociations
		// Verify that duplicates are eliminated.
		productAssociationService = new ProductAssociationServiceImpl() {
			// Stub out the compute filter method because we just want to test getProductAssociationsByType.
			@Override
			protected List<ProductAssociation> computeTopProductAssociations(final Set<ProductAssociation> allProductAssociations,
					final int maxAssociations) {
				return new ArrayList<ProductAssociation>(allProductAssociations);
			};
		};

		Set<StoreProduct> storeProductSet = new HashSet<StoreProduct>();
		storeProductSet.add(productA);
		storeProductSet.add(productB);
		Set<Product> productSet = new HashSet<Product>();
		final int maxAssociations = 5;
		List<ProductAssociation> productAssociations
			= productAssociationService.getProductAssociationsByType(storeProductSet, ProductAssociation.CROSS_SELL, maxAssociations, productSet);
		assertEquals("Unexpected number of associations", 1, productAssociations.size());
		assertEquals("Unexpected association", assocA, productAssociations.get(0));
	}
}
