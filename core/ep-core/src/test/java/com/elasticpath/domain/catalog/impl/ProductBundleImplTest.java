package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test that the methods of ProductBundleImpl behave as expected.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProductBundleImplTest {
	private ProductBundleImpl bundle;

	private static final Date FEBRUARY_28TH_2009 = getDate(2009, Calendar.FEBRUARY, 28);
	private static final Date JANUARY_28TH_2009 = getDate(2009, Calendar.JANUARY, 28);
	private static final Date OCT_28TH_2009 = getDate(2009, Calendar.OCTOBER, 28);

	private static final int NUMBER = 3;

	private static Date getDate(final int year, final int month, final int date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date);
		return calendar.getTime();
	}

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Setup required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		bundle = new ProductBundleImpl();
		setupConstituent(bundle);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private void setupConstituent(final ProductBundle bundle) {
		for (int i = 0; i < NUMBER; i++) {
			bundle.addConstituent(createBundleConstituent("constituent" + i, 0));
		}
	}

	private BundleConstituent createBundleConstituent(final String guid, final int ordering) {
		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setGuid(guid);
		constituent.setOrdering(ordering);

		return constituent;
	}

	/** . */
	@Test
	public void testOrderingIsCorrect() {
		assertConstituentsInOrder(bundle.getConstituents());
	}

	/** . */
	@Test
	public void testSwapFirstConstituentAndSecondConstituent() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		BundleConstituent first = constituents.get(0);
		BundleConstituent second = constituents.get(1);

		bundle.swap(first, second);

		assertEquals(1, constituents.indexOf(first));
		assertEquals(0, constituents.indexOf(second));
	}

	/** . */
	@Test
	public void testSwapPreservesOrdering() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		BundleConstituent first = constituents.get(0);
		BundleConstituent second = constituents.get(1);

		bundle.swap(first, second);

		assertConstituentsInOrder(constituents);

	}

	/** . */
	@Test
	public void testMoveFirstConstituentUp() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		BundleConstituent first = constituents.get(0);
		bundle.moveConstituentUp(first);

		first = constituents.get(0);
		assertEquals("constituent0", first.getGuid());
	}

	/** . */
	@Test
	public void testMoveSecondConstituentUp() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		BundleConstituent second = constituents.get(1);
		bundle.moveConstituentUp(second);

		BundleConstituent result0 = constituents.get(0);
		assertEquals("constituent1", result0.getGuid());
		BundleConstituent result1 = constituents.get(1);
		assertEquals("constituent0", result1.getGuid());
		BundleConstituent result2 = constituents.get(2);
		assertEquals("constituent2", result2.getGuid());
	}

	/** . */
	@Test
	public void testMoveConstituentUpWithNullConstituent() {
		List<BundleConstituent> before = new ArrayList<BundleConstituent>(bundle.getConstituents());
		bundle.moveConstituentUp(null);
		assertEquals(before, bundle.getConstituents());
	}

	/** . */
	@Test
	public void testMoveConstituentUpWithNonExistingConstituent() {
		List<BundleConstituent> before = new ArrayList<BundleConstituent>(bundle.getConstituents());
		bundle.moveConstituentUp(createBundleConstituent("mock", 2));
		assertEquals(before, bundle.getConstituents());
	}

	/** . */
	@Test
	public void testMoveLastConstituentDown() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		// move the first one up, nothing happens
		BundleConstituent last = constituents.get(2);
		bundle.moveConstituentDown(last);
		last = constituents.get(2);

		assertEquals("constituent2", last.getGuid());
	}

	/** . */
	@Test
	public void testMoveSecondConstituentDown() {
		List<BundleConstituent> constituents = bundle.getConstituents();

		// move the first one up, nothing happens
		BundleConstituent second = constituents.get(1);
		bundle.moveConstituentDown(second);

		BundleConstituent result0 = constituents.get(0);
		assertEquals("constituent0", result0.getGuid());

		BundleConstituent result1 = constituents.get(1);
		assertEquals("constituent2", result1.getGuid());

		BundleConstituent result2 = constituents.get(2);
		assertEquals("constituent1", result2.getGuid());
	}

	/** . */
	@Test
	public void testMoveConstituentDownWithNullConstituent() {
		List<BundleConstituent> before = new ArrayList<BundleConstituent>(bundle.getConstituents());
		bundle.moveConstituentDown(null);
		assertEquals(before, bundle.getConstituents());
	}

	/** . */
	@Test
	public void testMoveConstituentDownWithNonExistingConstituent() {
		List<BundleConstituent> before = new ArrayList<BundleConstituent>(bundle.getConstituents());
		bundle.moveConstituentDown(createBundleConstituent("mock", 2));
		assertEquals(before, bundle.getConstituents());
	}

	/** . */
	@Test
	public void testRemoveFirstConstituent() {
		bundle.removeConstituent(bundle.getConstituents().get(0));
		assertEquals(NUMBER - 1, bundle.getConstituents().size());
		assertConstituentsInOrder(bundle.getConstituents());
	}

	/** . */
	@Test
	public void testRemoveLastConstituent() {
		bundle.removeConstituent(bundle.getConstituents().get(2));
		assertEquals(NUMBER - 1, bundle.getConstituents().size());
		assertConstituentsInOrder(bundle.getConstituents());
	}

	/** . */
	@Test
	public void testRemoveSecondConstituentAndMoveUpFirstConstituent() {
		bundle.removeConstituent(bundle.getConstituents().get(1));
		bundle.moveConstituentDown(bundle.getConstituents().get(0));
		assertEquals(NUMBER - 1, bundle.getConstituents().size());

	}

	private void assertConstituentsInOrder(final List<BundleConstituent> constituents) {
		int index = 0;

		for (BundleConstituent constituent : constituents) {
			assertEquals(index, constituent.getOrdering().intValue());
			index++;
		}
	}

	/**
	 * Tests getAvailabilityCriteria() when there is one child which is AlwaysAvailable.
	 */
	@Test
	public void testOneChildAlwaysAvailable() {
		ProductBundle bundle = new ProductBundleImpl();

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		assertEquals("Should be the same as the the sole constituent", AvailabilityCriteria.ALWAYS_AVAILABLE, bundle.getAvailabilityCriteria());
	}

	/**
	 * Tests getAvailabilityCriteria() when there is one child which is AvailableForBackOrder.
	 * Note that we don't test every availability criteria - a few tests should be sufficient.
	 */
	@Test
	public void testOneChildBackOrder() {
		ProductBundle bundle = new ProductBundleImpl();

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		assertEquals("Should be the same as the the sole constituent",
				AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER, bundle
						.getAvailabilityCriteria());
	}

	/**
	 * Tests getAvailabilityCriteria() when there are two children: one with preorder and one with back order.
	 */
	@Test
	public void testAvailabilityPreOrderOverridesBackOrder() {
		ProductBundle bundle = new ProductBundleImpl();

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals("PreOrder has the highest priority", AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER, bundle.getAvailabilityCriteria());
	}

	/**
	 * Tests getAvailabilityCriteria() when there are two children: one with backorder and one with when in stock.
	 */
	@Test
	public void testAvailabilityBackOrderOverridesWhenInStcok() {
		ProductBundle bundle = new ProductBundleImpl();

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals("BackOrder has the highest priority", AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER, bundle.getAvailabilityCriteria());
	}

	/**
	 * Tests getAvailabilityCriteria() when there are two children: one with when in stock and one with always available.
	 */
	@Test
	public void testAvailabilityWhenInStockOverridesAlwaysAvailable() {
		ProductBundle bundle = new ProductBundleImpl();

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals("WhenInStock has the highest priority", AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, bundle.getAvailabilityCriteria());
	}

	/**	 */
	@Test
	public void testAvailabilityWhenInStockOverridesAlwaysAvailableWithNestedBundle() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const11 = new BundleConstituentImpl();
		Product child11 = new ProductImpl();
		child11.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const11.setConstituent(child11);
		child1.addConstituent(const11);

		assertEquals("WhenInStock has the highest priority", AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, bundle.getAvailabilityCriteria());
	}

	/** Test behaviour when there are no expected release dates set. */
	@Test
	public void testCalculateExpectedReleaseDateAllNull() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setExpectedReleaseDate(null);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setExpectedReleaseDate(null);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setExpectedReleaseDate(null);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertNull(bundle.getExpectedReleaseDate());
	}


	/** Tests that the latest date, on the bundle, takes precedence. */
	@Test
	public void testCalculateExpectedReleaseDateOnConstituent() {
		Date expectedReleaseDate = OCT_28TH_2009;

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setExpectedReleaseDate(JANUARY_28TH_2009);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setExpectedReleaseDate(FEBRUARY_28TH_2009);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setExpectedReleaseDate(expectedReleaseDate);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(expectedReleaseDate, bundle.getExpectedReleaseDate());
	}

	/** */
	@Test
	public void testExpectedReleaseDateWontBeOnBundleItself() {
		Date expectedReleaseDate = FEBRUARY_28TH_2009;

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setExpectedReleaseDate(OCT_28TH_2009);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setExpectedReleaseDate(JANUARY_28TH_2009);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setExpectedReleaseDate(expectedReleaseDate);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(FEBRUARY_28TH_2009, bundle.getExpectedReleaseDate());
	}

	/** */
	@Test
	public void testExpectedReleaseDateOnConstituentWithNestedBundleStructure() {
		Date expectedReleaseDate = OCT_28TH_2009;

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setExpectedReleaseDate(FEBRUARY_28TH_2009);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setExpectedReleaseDate(JANUARY_28TH_2009);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const11 = new BundleConstituentImpl();
		Product child11 = new ProductImpl();
		child11.setExpectedReleaseDate(expectedReleaseDate);
		const11.setConstituent(child11);
		child1.addConstituent(const11);

		assertEquals(expectedReleaseDate, bundle.getExpectedReleaseDate());
	}

	/** */
	@Test
	public void testBundleAndContsituentHidden() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(true);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setHidden(true);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleHiddenAndContsituentNotHidden() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setHidden(true);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleNotHiddenAndOneContsituentHidden() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setHidden(true);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleNotHiddenAndOneContsituentHiddenWithNestedStructure() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setHidden(false);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const11 = new BundleConstituentImpl();
		Product child11 = new ProductImpl();
		child11.setHidden(true);
		const11.setConstituent(child11);
		child1.addConstituent(const11);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleNotHiddenAndOneContsituentHiddenWithNestedStructure2() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setHidden(true);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const11 = new BundleConstituentImpl();
		Product child11 = new ProductImpl();
		child11.setHidden(false);
		const11.setConstituent(child11);
		child1.addConstituent(const11);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleHiddenAndContsituentsNotHidden() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(true);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setHidden(false);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(true, bundle.isHidden());
	}

	/** */
	@Test
	public void testBundleNotHiddenAndContsituentsNotHidden() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setHidden(false);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		assertEquals(false, bundle.isHidden());
	}

	/** */
	@Test
	public void testSetHiddenWithNestedStructure() {
		ProductBundle bundle = new ProductBundleImpl();
		bundle.setHidden(false);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setHidden(false);
		const1.setConstituent(child1);
		bundle.addConstituent(const1);

		BundleConstituent const11 = new BundleConstituentImpl();
		Product child11 = new ProductImpl();
		child11.setHidden(true);
		const11.setConstituent(child11);
		child1.addConstituent(const11);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setHidden(false);
		const2.setConstituent(child2);
		bundle.addConstituent(const2);

		bundle.setHidden(true);

		assertEquals(true, bundle.isBundleHidden());
		assertEquals(false, child1.isBundleHidden());
		assertEquals(true, child11.isHidden());
		assertEquals(true, child1.isHidden());
	}

	/**
	 * Test that the start date will come from a sku constituent not from its parent product.
	 */
	@Test
	public void testGetStartDateWithSku() {
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);

		ProductBundle bundleWithSku = new ProductBundleImpl();
		BundleConstituent constituent = new BundleConstituentImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setStartDate(FEBRUARY_28TH_2009);
		Product product = new ProductImpl();
		product.setStartDate(JANUARY_28TH_2009);
		sku.setProduct(product);
		constituent.setConstituent(sku);
		bundleWithSku.addConstituent(constituent);

		Date startDate = bundleWithSku.getStartDate();
		assertEquals("The bundle start date should be the sku's date", FEBRUARY_28TH_2009, startDate);
	}

	/**
	 * Test that the end date will come from a sku constituent not from its parent product.
	 */
	@Test
	public void testGetEndDateWithSku() {
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);

		ProductBundle bundleWithSku = new ProductBundleImpl();
		BundleConstituent constituent = new BundleConstituentImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setEndDate(FEBRUARY_28TH_2009);
		Product product = new ProductImpl();
		product.setEndDate(JANUARY_28TH_2009);
		sku.setProduct(product);
		constituent.setConstituent(sku);
		bundleWithSku.addConstituent(constituent);

		Date endDate = bundleWithSku.getEndDate();
		assertEquals("The bundle end date should be the sku's date", FEBRUARY_28TH_2009, endDate);
	}
}
