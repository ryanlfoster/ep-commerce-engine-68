package com.elasticpath.domain.catalog.impl;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;


/**
 * 
 * 
 * 
 * 
 * @author anewman
 *
 */
public class BundleConstituentImplTest {
	
	
	//private final Mockery context = new JUnit4Mockery();
	//private BeanFactory beanFactory;

	/**
	 * Setup required before each test.
	 */
	@Before
	public void setUp() {
	    //beanFactory = context.mock(BeanFactory.class);

	}

	/**
	 * Null should be returned if there is no adjustment for the given price list.
	 */
	@Test
	public void testGetPriceAdjustmentForPriceListReturnNullIfPriceListNotFound() {
		
		BundleConstituent constituent = new BundleConstituentImpl();

		String plGuid = "someGuid";
		
		
		PriceAdjustment priceAdjustment = constituent.getPriceAdjustmentForPriceList(plGuid);
		
		Assert.assertNull(priceAdjustment);
		
		
	}
	
	/**
	 * Return the correct adjustment for the given price list.
	 * 
	 */
	@Test
	public void testGetPriceAdjustmentForPriceListReturnCorrectPriceAdjustment() {
		
		BundleConstituent constituent = new BundleConstituentImpl();

		String plGuid = "someGuid";
		
		PriceAdjustment adjustment = new PriceAdjustmentImpl();
		
		adjustment.setPriceListGuid(plGuid);
		
		constituent.addPriceAdjustment(adjustment);
		
		PriceAdjustment returnedPriceAdjustment = constituent.getPriceAdjustmentForPriceList(plGuid);
		
		Assert.assertSame("price adjustments should be equal", adjustment, returnedPriceAdjustment);
		
		
	}
	
	/**
	 * Making sure the getPriceAdjustmentForPriceList will not throw an NPE.
	 */
	@Test
	public void testGetPriceAdjustmentForPriceListReturnNullForNullPlGuid() {
		BundleConstituent constituent = new BundleConstituentImpl();

		String plGuid = "someGuid";
		
		PriceAdjustment adjustment = new PriceAdjustmentImpl();
		
		adjustment.setPriceListGuid(plGuid);
		
		constituent.addPriceAdjustment(adjustment);
		
		PriceAdjustment returnedPriceAdjustment = constituent.getPriceAdjustmentForPriceList(null);
		Assert.assertNull(returnedPriceAdjustment);
	}
}
