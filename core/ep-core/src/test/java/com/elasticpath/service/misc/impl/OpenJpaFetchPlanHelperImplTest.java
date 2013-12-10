/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.FetchPlanImpl;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.jdbc.JDBCFetchPlan;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;

/**
 * Test cases for <code>OpenJPAFetchPlanHelperImpl</code>.
 * As a byproduct of testing whether load tuner methods are being called,
 * if the TestFetchPlan class is used correctly then all the fields being
 * added to the fetch plans will be verified to ensure that they actually
 * exist on the classes and that they are non-transient.
 */
public class OpenJpaFetchPlanHelperImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private OpenJPAFetchPlanHelperImpl fetchPlanHelperImpl;

	/**
	 * Set up mock objects required for the tests.
	 *
	 * @throws Exception -- in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		//Override so that we can check whether the persistence fields we specify adding
		//to fetch groups actually exist, because currently OpenJPA will happily allow you
		//to add fields to a fetch plan for an object even when those fields don't exist
		//on the object. If you happen to mistake the name of the getter on the object,
		//you won't know it unless you happen to have an integration test that fails somewhere.
		fetchPlanHelperImpl = new OpenJPAFetchPlanHelperImpl() {

			private final FetchPlan fetchPlan = new TestFetchPlan();

			@Override
			protected FetchPlan getFetchPlan() {
				return this.fetchPlan;

			}
		};
	}

	/**
	 * Test that clearing the fetch plan clears both fields and fetch groups.
	 */
	@Test
	public void testClearFetchPlan() {
		final OpenJPAEntityManager mockEntityManager = context.mock(OpenJPAEntityManager.class);
		final JDBCFetchPlan mockFetchPlan = context.mock(JDBCFetchPlan.class);
		final FetchPlan fetchPlan = mockFetchPlan;

		OpenJPAFetchPlanHelperImpl helper = new OpenJPAFetchPlanHelperImpl() {
			@Override
			protected OpenJPAEntityManager getOpenJPAEntityManager() {
				return mockEntityManager;
			}

		};

		context.checking(new Expectations() {
			{

				oneOf(mockEntityManager).getFetchPlan();
				will(returnValue(fetchPlan));
			}
		});
		assertEquals("The returned fetch plan should be the expected one", fetchPlan, helper.getFetchPlan());
		context.checking(new Expectations() {
			{

				oneOf(mockEntityManager).getFetchPlan();
				will(returnValue(fetchPlan));

				oneOf(mockFetchPlan).clearFields();
				oneOf(mockFetchPlan).resetFetchGroups();
			}
		});

		helper.clearFetchPlan();
	}

	/**
	 * Test that <code>configureCategoryFetchPlan()</code> inspects all flags of the load tuner.
	 */
	@Test
	public void testConfigureCategoryFetchPlanUsesLoadTuner() {
		final CategoryLoadTuner mockCategoryLoadTuner = context.mock(CategoryLoadTuner.class);
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryLoadTuner).isLoadingParent();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).isLoadingChildren();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).isLoadingMaster();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).isLoadingCategoryType();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).isLoadingAttributeValue();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).isLoadingLocaleDependantFields();
				will(returnValue(false));

				oneOf(mockCategoryLoadTuner).getCategoryTypeLoadTuner();
				will(returnValue(null));
			}
		});

		fetchPlanHelperImpl.configureCategoryFetchPlan(mockCategoryLoadTuner);
	}

	/**
	 * Test that <code>configureCategoryFetchPlan()</code> adds fields to the fetch plan.
	 */
	@Test
	public void testConfigureCategoryFetchPlanAddsFields() {
		final CategoryLoadTuner mockCategoryLoadTuner = context.mock(CategoryLoadTuner.class);
		context.checking(new Expectations() {
			{

				oneOf(mockCategoryLoadTuner).isLoadingParent();
				will(returnValue(true));

				oneOf(mockCategoryLoadTuner).isLoadingMaster();
				will(returnValue(true));

				oneOf(mockCategoryLoadTuner).isLoadingCategoryType();
				will(returnValue(true));

				oneOf(mockCategoryLoadTuner).isLoadingAttributeValue();
				will(returnValue(true));

				oneOf(mockCategoryLoadTuner).isLoadingLocaleDependantFields();
				will(returnValue(true));
			}
		});

		// This flag should result in a call to getChildCategoryLevel and addFetchGroup
		context.checking(new Expectations() {
			{
				oneOf(mockCategoryLoadTuner).isLoadingChildren();
				will(returnValue(true));

				oneOf(mockCategoryLoadTuner).getChildCategoryLevel();
				will(returnValue(1));

				oneOf(mockCategoryLoadTuner).getParentCategoryLevel();
				will(returnValue(1));


				oneOf(mockCategoryLoadTuner).getCategoryTypeLoadTuner();
				will(returnValue(null));
			}
		});

		fetchPlanHelperImpl.configureCategoryFetchPlan(mockCategoryLoadTuner);
	}

	/**
	 * Test that <code>configureProductAssociationFetchPlan()</code> inspects all flags of the load tuner.
	 */
	@Test
	public void testConfigureProductAssociationUsesLoadTuner() {
		final ProductAssociationLoadTuner mockProductAssociationLoadTuner = context.mock(ProductAssociationLoadTuner.class);
		context.checking(new Expectations() {
			{
				oneOf(mockProductAssociationLoadTuner).isLoadingCatalog();
				will(returnValue(false));

				oneOf(mockProductAssociationLoadTuner).getProductLoadTuner();
				will(returnValue(null));
			}
		});

		fetchPlanHelperImpl.configureProductAssociationFetchPlan(mockProductAssociationLoadTuner);
	}

	/**
	 * Test that the ProductSku load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductSkuFetchPlanUsesLoadTuner() {
		final ProductSkuLoadTuner mockProductSkuLoadTuner = context.mock(ProductSkuLoadTuner.class);
		context.checking(new Expectations() {
			{

				oneOf(mockProductSkuLoadTuner).isLoadingAttributeValue();
				will(returnValue(true));

				oneOf(mockProductSkuLoadTuner).isLoadingOptionValue();
				will(returnValue(true));

				oneOf(mockProductSkuLoadTuner).isLoadingProduct();
				will(returnValue(true));

				oneOf(mockProductSkuLoadTuner).isLoadingDigitalAsset();
				will(returnValue(true));
			}
		});

		fetchPlanHelperImpl.configureProductSkuFetchPlan(mockProductSkuLoadTuner);
	}

	/**
	 * Test that the ProductType load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductTypeFetchPlanUsesLoadTuner() {
		final ProductTypeLoadTuner mockProductTypeLoadTuner = context.mock(ProductTypeLoadTuner.class);
		context.checking(new Expectations() {
			{

				oneOf(mockProductTypeLoadTuner).isLoadingAttributes();
				will(returnValue(true));

				oneOf(mockProductTypeLoadTuner).isLoadingSkuOptions();
				will(returnValue(true));
			}
		});

		fetchPlanHelperImpl.configureProductTypeFetchPlan(mockProductTypeLoadTuner);
	}

	/**
	 * Test that the Product load tuner is queried for all fields.
	 */
	@Test
	public void testConfigureProductFetchPlanUsesLoadTuner() {
		final ProductLoadTuner mockProductLoadTuner = context.mock(ProductLoadTuner.class);
		context.checking(new Expectations() {
			{

				oneOf(mockProductLoadTuner).isLoadingAttributeValue();
				will(returnValue(true));

				oneOf(mockProductLoadTuner).isLoadingCategories();
				will(returnValue(true));

				oneOf(mockProductLoadTuner).isLoadingDefaultSku();
				will(returnValue(true));

				oneOf(mockProductLoadTuner).isLoadingProductType();
				will(returnValue(true));

				oneOf(mockProductLoadTuner).isLoadingSkus();
				will(returnValue(true));
			}
		});
		//for product categories
		context.checking(new Expectations() {
			{
				oneOf(mockProductLoadTuner).getCategoryLoadTuner();
				will(returnValue(null));
			}
		});
		//once for loading skus, once for loading default sku
		context.checking(new Expectations() {
			{
				exactly(2).of(mockProductLoadTuner).getProductSkuLoadTuner();
				will(returnValue(null));
			}
		});
		//for product type
		context.checking(new Expectations() {
			{
				oneOf(mockProductLoadTuner).getProductTypeLoadTuner();
				will(returnValue(null));
			}
		});

		fetchPlanHelperImpl.configureProductFetchPlan(mockProductLoadTuner);
	}

	/**
	 * Test class that checks the fields we add to a fetch plan actually exist,
	 * and that they're not transient.
	 */
	public class TestFetchPlan extends FetchPlanImpl {

		/**
		 * Constructor that assumes we don't need a real FetchPlan, so passes in null to its constructor.
		 */
		public TestFetchPlan() {
			super(null);
		}
		/**
		 * Doesn't actually add a field to the fetch plan, but instead checks that the field exists
		 * on the given class. Also checks that the field specified is not marked as Transient.
		 * @param cls the class to which the field would be added
		 * @param field the name of the persistent field on the given class
		 * @return the fetch plan
		 */
		@Override
		@SuppressWarnings({"unchecked", "PMD.AvoidThrowingRawExceptionTypes", "rawtypes"})
		public FetchPlan addField(final Class cls, final String field) {
			Class<? extends Annotation> transientAnnotation = null;
			try {
				transientAnnotation = (Class<? extends Annotation>) Class.forName("javax.persistence.Transient");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Exception running test - class javax.persistence.Transient not found in classpath", e);
			}

			try {
				Method method = cls.getDeclaredMethod("get" + StringUtils.capitalize(field));
				if (method.getAnnotation(transientAnnotation) != null) {
					fail("Persistence field " + field + " in class " + cls.getName() + " is declared Transient!");
				}
			} catch (NoSuchMethodException snme) {
				fail("Persistence field " + field + " not defined for class " + cls.getName());
			}
			return this;
		}

		/**
		 * Does nothing.
		 * @param fetchGroupName the name of the fetch group to add to the fetch plan
		 * @return the fetch plan
		 */
		@Override
		public FetchPlan addFetchGroup(final String fetchGroupName) {
			return this; //ignore calls like this, can't test them easily.
		}
	}
}
