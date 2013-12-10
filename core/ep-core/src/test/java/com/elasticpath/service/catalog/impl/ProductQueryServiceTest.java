/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.CategoryLoadTunerImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.impl.UnsupportedResultTypeException;
import com.elasticpath.service.query.relations.BrandRelation;
import com.elasticpath.service.query.relations.CategoryRelation;
import com.elasticpath.service.query.relations.ProductRelation;
import com.elasticpath.service.query.relations.ProductSkuRelation;
import com.elasticpath.service.query.relations.StoreRelation;

/**
 * Test that {@see ProductQueryService} behaves as expected.
 */
public class ProductQueryServiceTest {

	private static final long ABOUT_TWO_MONTHS = 1000L * 60L * 60L * 24L * 30L * 2L;

	private static final String GUID_1234 = "1234";

	private static final String GUID_2133 = "2133";

	private static final long UID_100L = 100L;

	private static final long UID_101L = 101L;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;
	private ProductQueryService productQueryService;
	private FetchPlanHelper fetchPlanHelper;

	/**
	 * Setup required before each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		productQueryService = new ProductQueryService();

		persistenceEngine = context.mock(PersistenceEngine.class);
		productQueryService.setPersistenceEngine(persistenceEngine);

		beanFactory = context.mock(BeanFactory.class);
		productQueryService.setBeanFactory(beanFactory);

		fetchPlanHelper = context.mock(FetchPlanHelper.class);
		productQueryService.setFetchPlanHelper(fetchPlanHelper);
	}

	/**
	 * Test query for product by GUID.
	 */
	@Test
	public void testQueryForProductByGuid() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.code = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product by GUID.
	 */
	@Test
	public void testQueryForProductByGuidWithSingleResult() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.code = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product by GUID where the product by the GUID does not exist.
	 */
	@Test
	public void testQueryForProductByGuidWithNonExistentSingleResult() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.code = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product by multiple GUIDs.
	 */
	@Test
	public void testQueryForProductByMultipleGuids() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234, GUID_2133))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { Arrays.asList(GUID_1234, GUID_2133) };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.code IN (?1)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product UID by multiple GUIDs.
	 */
	@Test
	public void testQueryForProductUidByMultipleGuids() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234, GUID_2133))
			.returning(ResultType.UID);

		final Object[] parameters = new Object[] { Arrays.asList(GUID_1234, GUID_2133) };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.uidPk FROM ProductImpl AS p WHERE p.code IN (?1)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product by multiple UIDs.
	 */
	@Test
	public void testQueryForProductByMultipleUids() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().uids(UID_100L, UID_101L))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { Arrays.asList(UID_100L, UID_101L) };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.uidPk IN (?1)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product GUID exists.
	 */
	@Test
	public void testQueryForProductGuidExists() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.CONDITIONAL);

		final List<Long> results = new ArrayList<Long>();
		results.add(1L);

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT count(p) FROM ProductImpl AS p WHERE p.code = ?1", parameters);
				will(returnValue(results));
			}
		});

		QueryResult<Boolean> result = productQueryService.query(criteria);
		assertTrue("The result should be true", result.getSingleResult());
	}

	/**
	 * Test query for when a product GUID does not exist.
	 */
	@Test
	public void testQueryForProductGuidNotExists() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.CONDITIONAL);

		final List<Long> results = new ArrayList<Long>();

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT count(p) FROM ProductImpl AS p WHERE p.code = ?1", parameters);
				will(returnValue(results));
			}
		});

		QueryResult<Boolean> result = productQueryService.query(criteria);
		assertFalse("The result should be false", result.getSingleResult());
	}

	/**
	 * Test query product after modified date.
	 */
	@Test
	public void testQueryProductAfterModifiedDate() {
		final Date now = new Date();
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).modifiedAfter(now).returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { now };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.lastModifiedDate >= ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query product is within a date range.
	 */
	@Test
	public void testQueryProductInDateRange() {
		final Date now = new Date();
		final Date later = new Date(now.getTime() + ABOUT_TWO_MONTHS);
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).inDateRange(now, later).returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { now, later };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p WHERE p.startDate <= ?1 AND "
						+ "(p.endDate IS NULL OR p.endDate >= ?2)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for GUID with brand UID.
	 */
	@Test
	public void testQueryForGuidWithBrandUid() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(BrandRelation.having().uids(UID_100L))
			.returning(ResultType.GUID);

		final Object[] parameters = new Object[] { UID_100L };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.code FROM ProductImpl AS p WHERE p.brand.uidPk = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for GUID with multiple brand UIDs.
	 */
	@Test
	public void testQueryForGuidWithMultipleBrandUids() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(BrandRelation.having().uids(UID_100L, UID_101L))
			.returning(ResultType.GUID);

		final Object[] parameters = new Object[] { Arrays.asList(UID_100L, UID_101L) };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.code FROM ProductImpl AS p WHERE p.brand.uidPk IN (?1)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for all with product load tuner.
	 */
	@Test
	public void testQueryForAllWithProductLoadTuner() {
		final ProductLoadTuner productLoadTuner = new ProductLoadTunerImpl();
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).usingLoadTuner(productLoadTuner).returning(ResultType.ENTITY);

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureProductFetchPlan(productLoadTuner);

				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p");
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for all with fetch group load tuner.
	 */
	@Test
	public void testQueryForAllWithFetchGroupLoadTuner() {
		final FetchGroupLoadTuner fetchGroupLoadTuner = new FetchGroupLoadTunerImpl();
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).usingLoadTuner(fetchGroupLoadTuner).returning(ResultType.ENTITY);

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(fetchPlanHelper).configureFetchGroupLoadTuner(fetchGroupLoadTuner);

				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p");
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for all with unexpected load tuner.
	 */
	@Test(expected = EpServiceException.class)
	public void testQueryForAllWithUnexpectedLoadTuner() {
		final CategoryLoadTuner categoryLoadTuner = new CategoryLoadTunerImpl();
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).usingLoadTuner(categoryLoadTuner).returning(ResultType.ENTITY);

		shouldCreateProductAndClearFetchPlan();

		productQueryService.query(criteria);
	}

	/**
	 * Test using null result type.
	 */
	@Test(expected = UnsupportedResultTypeException.class)
	public void testUsingNullResultType() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).returning(null);

		productQueryService.query(criteria);
	}

	/**
	 * Test query for UIDs.
	 */
	@Test
	public void testQueryForUids() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).returning(ResultType.UID);

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.uidPk FROM ProductImpl AS p");
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test find by category uid.
	 */
	@Test
	public void testFindByCategoryUid() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(CategoryRelation.having().uids(UID_100L))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { UID_100L };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine)
					.retrieve("SELECT p FROM ProductImpl AS p JOIN p.productCategories AS pc WHERE pc.category.uidPk = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test find by store uid.
	 */
	@Test
	public void testFindByStoreUid() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(StoreRelation.having().uids(UID_100L))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { UID_100L };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBeanImplClass(ContextIdNames.STORE); will(returnValue(StoreImpl.class));
				oneOf(persistenceEngine).retrieve("SELECT p FROM ProductImpl AS p JOIN p.productCategories AS pc,"
						+ " StoreImpl AS s WHERE pc.category.catalog = s.catalog AND s.uidPk = ?1",
					parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test find available uids.
	 */
	@Test
	public void testFindAvailableUids() {
		final Date now = new Date();
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).inDateRange(now, now).returning(ResultType.UID);

		final Object[] parameters = new Object[] { now, now };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.uidPk FROM ProductImpl AS p WHERE p.startDate <= ?1 AND "
						+ "(p.endDate IS NULL OR p.endDate >= ?2)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test find available uids by modified date.
	 */
	@Test
	public void testFindAvailableUidsByModifiedDate() {
		final Date now = new Date();
		final Date later = new Date(now.getTime() + ABOUT_TWO_MONTHS);
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).modifiedAfter(later).inDateRange(now, now)
			.returning(ResultType.UID);

		final Object[] parameters = new Object[] { later, now, now };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.uidPk FROM ProductImpl AS p WHERE p.lastModifiedDate >= ?1 "
						+ "AND p.startDate <= ?2 AND (p.endDate IS NULL OR p.endDate >= ?3)", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test query for product by GUID.
	 */
	@Test
	public void testQueryForLastModifiedDateByGuid() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductRelation.having().codes(GUID_1234))
			.returning(ResultType.DATE);

		final Object[] parameters = new Object[] { GUID_1234 };

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT p.lastModifiedDate FROM ProductImpl AS p WHERE p.code = ?1", parameters);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Test find uid by sku code.
	 */
	@Test
	public void testFindUidBySkuCode() {
		QueryCriteria<Product> criteria = CriteriaBuilder.criteriaFor(Product.class).with(ProductSkuRelation.having().codes("sku123"))
			.returning(ResultType.UID);

		final String parameter = "sku123";

		shouldCreateProductAndClearFetchPlan();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve(
						"SELECT p.uidPk FROM ProductImpl AS p JOIN p.productSkusInternal AS ps WHERE ps.skuCodeInternal = ?1", parameter);
			}
		});

		productQueryService.query(criteria);
	}

	/**
	 * Should create product and clear fetch plan.
	 */
	private void shouldCreateProductAndClearFetchPlan() {
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBeanImplClass(ContextIdNames.PRODUCT); will(returnValue(ProductImpl.class));
				allowing(fetchPlanHelper).clearFetchPlan();
			}
		});
	}

}
